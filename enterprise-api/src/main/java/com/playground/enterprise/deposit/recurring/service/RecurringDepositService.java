package com.playground.enterprise.deposit.recurring.service;

import com.playground.enterprise.audit.service.EnterpriseAuditService;
import com.playground.enterprise.deposit.recurring.dto.CreateRecurringDepositRequest;
import com.playground.enterprise.deposit.recurring.dto.InstallmentResponse;
import com.playground.enterprise.deposit.recurring.dto.RecurringDepositResponse;
import com.playground.enterprise.deposit.recurring.entity.RecurringDepositEntity;
import com.playground.enterprise.deposit.recurring.entity.RecurringDepositInstallmentEntity;
import com.playground.enterprise.deposit.recurring.repository.RecurringDepositInstallmentRepository;
import com.playground.enterprise.deposit.recurring.repository.RecurringDepositRepository;
import com.playground.enterprise.domain.DepositFrequency;
import com.playground.enterprise.domain.DepositStatus;
import com.playground.enterprise.domain.InstallmentStatus;
import com.playground.enterprise.event.service.EventOutboxService;
import com.playground.common.dto.PageResponse;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class RecurringDepositService {

    private final RecurringDepositRepository recurringDepositRepository;
    private final RecurringDepositInstallmentRepository installmentRepository;
    private final EnterpriseAuditService auditService;
    private final EventOutboxService eventOutboxService;

    public RecurringDepositService(
            RecurringDepositRepository recurringDepositRepository,
            RecurringDepositInstallmentRepository installmentRepository,
            EnterpriseAuditService auditService,
            EventOutboxService eventOutboxService) {
        this.recurringDepositRepository = recurringDepositRepository;
        this.installmentRepository = installmentRepository;
        this.auditService = auditService;
        this.eventOutboxService = eventOutboxService;
    }

    @Transactional
    public RecurringDepositResponse create(String tenantId, String actorId, CreateRecurringDepositRequest request) {
        Instant now = Instant.now();
        String depositId = UUID.randomUUID().toString();
        LocalDate firstDueDate = nextDueDate(LocalDate.now(), request.frequency());

        RecurringDepositEntity deposit = new RecurringDepositEntity();
        deposit.setId(depositId);
        deposit.setTenantId(tenantId);
        deposit.setPartyId(request.partyId());
        deposit.setAccountId(request.accountId());
        deposit.setInstallmentAmount(request.installmentAmount());
        deposit.setCurrency(request.currency());
        deposit.setFrequency(request.frequency());
        deposit.setStatus(DepositStatus.ACTIVE);
        deposit.setNextDueDate(firstDueDate);
        deposit.setCreatedAt(now);
        recurringDepositRepository.save(deposit);

        RecurringDepositInstallmentEntity installment = new RecurringDepositInstallmentEntity();
        installment.setId(UUID.randomUUID().toString());
        installment.setTenantId(tenantId);
        installment.setRecurringDepositId(depositId);
        installment.setAmount(request.installmentAmount());
        installment.setStatus(InstallmentStatus.PENDING);
        installment.setDueDate(firstDueDate);
        installment.setCreatedAt(now);
        installmentRepository.save(installment);

        auditService.record(tenantId, "RECURRING_DEPOSIT", depositId, "CREATED", actorId,
                "installmentAmount=" + request.installmentAmount() + " " + request.currency());
        eventOutboxService.publish(tenantId, "RECURRING_DEPOSIT", depositId, "RECURRING_DEPOSIT_CREATED",
                "partyId=" + request.partyId());

        return toResponse(deposit);
    }

    @Transactional(readOnly = true)
    public RecurringDepositResponse get(String tenantId, String depositId) {
        RecurringDepositEntity deposit = recurringDepositRepository.findByIdAndTenantId(depositId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurring deposit not found"));
        return toResponse(deposit);
    }

    @Transactional(readOnly = true)
    public PageResponse<RecurringDepositResponse> listByParty(String tenantId, String partyId, int page, int size) {
        Page<RecurringDepositEntity> result = recurringDepositRepository.findByTenantIdAndPartyId(
                tenantId, partyId, PageRequest.of(page, size));
        return new PageResponse<>(
                result.getContent().stream().map(RecurringDepositService::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), null);
    }

    @Transactional
    public InstallmentResponse recordInstallment(String tenantId, String actorId, String depositId) {
        RecurringDepositEntity deposit = recurringDepositRepository.findByIdAndTenantId(depositId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurring deposit not found"));
        if (deposit.getStatus() != DepositStatus.ACTIVE) {
            throw new BadRequestException("Recurring deposit is not active");
        }

        RecurringDepositInstallmentEntity installment = installmentRepository
                .findByTenantIdAndRecurringDepositIdAndStatus(
                        tenantId, depositId, InstallmentStatus.PENDING, PageRequest.of(0, 1))
                .getContent()
                .stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("No pending installment found"));

        Instant now = Instant.now();
        installment.setStatus(InstallmentStatus.PAID);
        installment.setPaidAt(now);
        installmentRepository.save(installment);

        LocalDate nextDue = nextDueDate(installment.getDueDate(), deposit.getFrequency());
        deposit.setNextDueDate(nextDue);
        recurringDepositRepository.save(deposit);

        RecurringDepositInstallmentEntity nextInstallment = new RecurringDepositInstallmentEntity();
        nextInstallment.setId(UUID.randomUUID().toString());
        nextInstallment.setTenantId(tenantId);
        nextInstallment.setRecurringDepositId(depositId);
        nextInstallment.setAmount(deposit.getInstallmentAmount());
        nextInstallment.setStatus(InstallmentStatus.PENDING);
        nextInstallment.setDueDate(nextDue);
        nextInstallment.setCreatedAt(now);
        installmentRepository.save(nextInstallment);

        auditService.record(tenantId, "RECURRING_DEPOSIT_INSTALLMENT", installment.getId(), "PAID", actorId,
                "recurringDepositId=" + depositId);
        eventOutboxService.publish(tenantId, "RECURRING_DEPOSIT", depositId, "INSTALLMENT_RECORDED",
                "installmentId=" + installment.getId());

        return toResponse(installment);
    }

    private static LocalDate nextDueDate(LocalDate from, DepositFrequency frequency) {
        return switch (frequency) {
            case MONTHLY -> from.plusMonths(1);
            case QUARTERLY -> from.plusMonths(3);
        };
    }

    static RecurringDepositResponse toResponse(RecurringDepositEntity deposit) {
        return new RecurringDepositResponse(
                deposit.getId(), deposit.getPartyId(), deposit.getAccountId(),
                deposit.getInstallmentAmount(), deposit.getCurrency(), deposit.getFrequency(),
                deposit.getStatus(), deposit.getNextDueDate(), deposit.getCreatedAt());
    }

    static InstallmentResponse toResponse(RecurringDepositInstallmentEntity installment) {
        return new InstallmentResponse(
                installment.getId(), installment.getRecurringDepositId(), installment.getAmount(),
                installment.getStatus(), installment.getDueDate(), installment.getPaidAt(),
                installment.getCreatedAt());
    }
}
