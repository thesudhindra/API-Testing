package com.playground.banking.kyc.service;

import com.playground.banking.customer.service.PartyService;
import com.playground.banking.domain.KycStatus;
import com.playground.banking.kyc.dto.KycCaseResponse;
import com.playground.banking.kyc.dto.ReviewKycRequest;
import com.playground.banking.kyc.dto.SubmitKycRequest;
import com.playground.banking.kyc.entity.KycCaseEntity;
import com.playground.banking.kyc.repository.KycCaseRepository;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ConflictException;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class KycService {

    private static final String DEFAULT_LEVEL = "STANDARD";

    private final KycCaseRepository kycCaseRepository;
    private final PartyService partyService;

    public KycService(KycCaseRepository kycCaseRepository, PartyService partyService) {
        this.kycCaseRepository = kycCaseRepository;
        this.partyService = partyService;
    }

    @Transactional(readOnly = true)
    public List<KycCaseResponse> listByParty(String tenantId, String partyId) {
        partyService.requirePartyInTenant(tenantId, partyId);
        return kycCaseRepository.findByTenantIdAndPartyId(tenantId, partyId).stream()
                .map(KycService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public KycCaseResponse getCase(String tenantId, String caseId) {
        KycCaseEntity kycCase = kycCaseRepository.findByIdAndTenantId(caseId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC case not found"));
        return toResponse(kycCase);
    }

    @Transactional
    public KycCaseResponse submit(String tenantId, SubmitKycRequest request) {
        partyService.requirePartyInTenant(tenantId, request.partyId());

        boolean hasOpenCase = kycCaseRepository.findByTenantIdAndPartyId(tenantId, request.partyId()).stream()
                .anyMatch(c -> c.getStatus() == KycStatus.OPEN
                        || c.getStatus() == KycStatus.IN_REVIEW
                        || c.getStatus() == KycStatus.PENDING_DOCUMENTS);
        if (hasOpenCase) {
            throw new ConflictException("An open KYC case already exists for this party");
        }

        Instant now = Instant.now();
        KycCaseEntity kycCase = new KycCaseEntity();
        kycCase.setId("kyc-" + UUID.randomUUID());
        kycCase.setPartyId(request.partyId());
        kycCase.setStatus(KycStatus.OPEN);
        kycCase.setLevel(DEFAULT_LEVEL);
        kycCase.setCreatedAt(now);
        kycCase.setUpdatedAt(now);

        return toResponse(kycCaseRepository.save(kycCase));
    }

    @Transactional
    public KycCaseResponse review(String tenantId, String caseId, ReviewKycRequest request) {
        if (request.status() != KycStatus.APPROVED && request.status() != KycStatus.REJECTED) {
            throw new BadRequestException("Review status must be APPROVED or REJECTED");
        }

        KycCaseEntity kycCase = kycCaseRepository.findByIdAndTenantId(caseId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC case not found"));

        if (kycCase.getStatus() == KycStatus.APPROVED || kycCase.getStatus() == KycStatus.REJECTED) {
            throw new ConflictException("KYC case is already in a terminal state");
        }

        kycCase.setStatus(request.status());
        kycCase.setDecisionReason(request.decisionReason());
        kycCase.setUpdatedAt(Instant.now());

        return toResponse(kycCaseRepository.save(kycCase));
    }

    @Transactional(readOnly = true)
    public boolean hasApprovedKyc(String tenantId, String partyId) {
        partyService.requirePartyInTenant(tenantId, partyId);
        return kycCaseRepository
                .findFirstByPartyIdAndStatusOrderByCreatedAtDesc(partyId, KycStatus.APPROVED)
                .isPresent();
    }

    static KycCaseResponse toResponse(KycCaseEntity kycCase) {
        return new KycCaseResponse(
                kycCase.getId(),
                kycCase.getPartyId(),
                kycCase.getStatus(),
                kycCase.getLevel(),
                kycCase.getDecisionReason(),
                kycCase.getCreatedAt(),
                kycCase.getUpdatedAt());
    }
}
