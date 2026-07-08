package com.playground.enterprise.deposit.fixed.service;

import com.playground.enterprise.audit.service.EnterpriseAuditService;
import com.playground.enterprise.deposit.fixed.dto.CreateFixedDepositRequest;
import com.playground.enterprise.deposit.fixed.dto.FixedDepositResponse;
import com.playground.enterprise.deposit.fixed.entity.FixedDepositEntity;
import com.playground.enterprise.deposit.fixed.repository.FixedDepositRepository;
import com.playground.enterprise.domain.DepositStatus;
import com.playground.enterprise.event.service.EventOutboxService;
import com.playground.common.dto.PageResponse;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class FixedDepositService {

    private final FixedDepositRepository fixedDepositRepository;
    private final EnterpriseAuditService auditService;
    private final EventOutboxService eventOutboxService;

    public FixedDepositService(
            FixedDepositRepository fixedDepositRepository,
            EnterpriseAuditService auditService,
            EventOutboxService eventOutboxService) {
        this.fixedDepositRepository = fixedDepositRepository;
        this.auditService = auditService;
        this.eventOutboxService = eventOutboxService;
    }

    @Transactional
    public FixedDepositResponse create(String tenantId, String actorId, CreateFixedDepositRequest request) {
        Instant now = Instant.now();
        String depositId = UUID.randomUUID().toString();

        FixedDepositEntity deposit = new FixedDepositEntity();
        deposit.setId(depositId);
        deposit.setTenantId(tenantId);
        deposit.setPartyId(request.partyId());
        deposit.setAccountId(request.accountId());
        deposit.setPrincipal(request.principal());
        deposit.setCurrency(request.currency());
        deposit.setInterestRate(request.interestRate());
        deposit.setTermDays(request.termDays());
        deposit.setMaturityDate(LocalDate.now().plusDays(request.termDays()));
        deposit.setStatus(DepositStatus.ACTIVE);
        deposit.setCreatedAt(now);
        fixedDepositRepository.save(deposit);

        auditService.record(tenantId, "FIXED_DEPOSIT", depositId, "CREATED", actorId,
                "principal=" + request.principal() + " " + request.currency());
        eventOutboxService.publish(tenantId, "FIXED_DEPOSIT", depositId, "FIXED_DEPOSIT_CREATED",
                "partyId=" + request.partyId() + ",principal=" + request.principal());

        return toResponse(deposit);
    }

    @Transactional(readOnly = true)
    public FixedDepositResponse get(String tenantId, String depositId) {
        FixedDepositEntity deposit = fixedDepositRepository.findByIdAndTenantId(depositId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed deposit not found"));
        return toResponse(deposit);
    }

    @Transactional(readOnly = true)
    public PageResponse<FixedDepositResponse> listByParty(String tenantId, String partyId, int page, int size) {
        Page<FixedDepositEntity> result = fixedDepositRepository.findByTenantIdAndPartyId(
                tenantId, partyId, PageRequest.of(page, size));
        return new PageResponse<>(
                result.getContent().stream().map(FixedDepositService::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), null);
    }

    static FixedDepositResponse toResponse(FixedDepositEntity deposit) {
        return new FixedDepositResponse(
                deposit.getId(), deposit.getPartyId(), deposit.getAccountId(),
                deposit.getPrincipal(), deposit.getCurrency(), deposit.getInterestRate(),
                deposit.getTermDays(), deposit.getMaturityDate(), deposit.getStatus(), deposit.getCreatedAt());
    }
}
