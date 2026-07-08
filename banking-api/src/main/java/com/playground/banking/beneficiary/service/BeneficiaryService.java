package com.playground.banking.beneficiary.service;

import com.playground.banking.beneficiary.dto.BeneficiaryResponse;
import com.playground.banking.beneficiary.dto.CreateBeneficiaryRequest;
import com.playground.banking.beneficiary.entity.BeneficiaryEntity;
import com.playground.banking.beneficiary.repository.BeneficiaryRepository;
import com.playground.banking.customer.service.PartyService;
import com.playground.banking.domain.BeneficiaryStatus;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final PartyService partyService;

    public BeneficiaryService(BeneficiaryRepository beneficiaryRepository, PartyService partyService) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.partyService = partyService;
    }

    @Transactional(readOnly = true)
    public List<BeneficiaryResponse> listByParty(String tenantId, String partyId) {
        partyService.requirePartyInTenant(tenantId, partyId);
        return beneficiaryRepository.findByTenantIdAndPartyIdAndStatusNot(
                        tenantId, partyId, BeneficiaryStatus.DELETED).stream()
                .map(BeneficiaryService::toResponse)
                .toList();
    }

    @Transactional
    public BeneficiaryResponse create(String tenantId, CreateBeneficiaryRequest request) {
        partyService.requirePartyInTenant(tenantId, request.partyId());

        if ((request.accountNumber() == null || request.accountNumber().isBlank())
                && (request.iban() == null || request.iban().isBlank())) {
            throw new BadRequestException("Either accountNumber or iban is required");
        }

        BeneficiaryEntity beneficiary = new BeneficiaryEntity();
        beneficiary.setId("ben-" + UUID.randomUUID());
        beneficiary.setTenantId(tenantId);
        beneficiary.setPartyId(request.partyId());
        beneficiary.setNickname(request.nickname());
        beneficiary.setSortCode(request.sortCode());
        beneficiary.setAccountNumber(request.accountNumber());
        beneficiary.setIban(request.iban());
        beneficiary.setStatus(BeneficiaryStatus.PENDING_VERIFICATION);
        beneficiary.setCreatedAt(Instant.now());

        return toResponse(beneficiaryRepository.save(beneficiary));
    }

    @Transactional(readOnly = true)
    public BeneficiaryResponse get(String tenantId, String beneficiaryId) {
        BeneficiaryEntity beneficiary = beneficiaryRepository.findByIdAndTenantId(beneficiaryId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found"));
        return toResponse(beneficiary);
    }

    @Transactional
    public void delete(String tenantId, String beneficiaryId) {
        BeneficiaryEntity beneficiary = beneficiaryRepository.findByIdAndTenantId(beneficiaryId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found"));
        beneficiary.setStatus(BeneficiaryStatus.DELETED);
        beneficiaryRepository.save(beneficiary);
    }

    static BeneficiaryResponse toResponse(BeneficiaryEntity beneficiary) {
        return new BeneficiaryResponse(
                beneficiary.getId(),
                beneficiary.getPartyId(),
                beneficiary.getNickname(),
                beneficiary.getSortCode(),
                beneficiary.getAccountNumber(),
                beneficiary.getIban(),
                beneficiary.getStatus(),
                beneficiary.getVerifiedAt(),
                beneficiary.getFirstPaymentAllowedAfter(),
                beneficiary.getCreatedAt());
    }
}
