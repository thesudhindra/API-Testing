package com.playground.enterprise.aml.service;

import com.playground.enterprise.aml.dto.AmlCaseResponse;
import com.playground.enterprise.aml.dto.AmlScreeningResponse;
import com.playground.enterprise.aml.dto.CreateAmlCaseRequest;
import com.playground.enterprise.aml.dto.AmlScreeningRequest;
import com.playground.enterprise.aml.entity.AmlCaseEntity;
import com.playground.enterprise.aml.entity.AmlScreeningEntity;
import com.playground.enterprise.aml.repository.AmlCaseRepository;
import com.playground.enterprise.aml.repository.AmlScreeningRepository;
import com.playground.enterprise.audit.service.EnterpriseAuditService;
import com.playground.enterprise.domain.AmlCaseStatus;
import com.playground.enterprise.domain.AmlCaseType;
import com.playground.enterprise.domain.AmlPriority;
import com.playground.enterprise.domain.ScreeningResult;
import com.playground.enterprise.domain.ScreeningType;
import com.playground.enterprise.event.service.EventOutboxService;
import com.playground.common.dto.PageResponse;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AmlService {

    private final AmlCaseRepository amlCaseRepository;
    private final AmlScreeningRepository amlScreeningRepository;
    private final EnterpriseAuditService auditService;
    private final EventOutboxService eventOutboxService;

    public AmlService(
            AmlCaseRepository amlCaseRepository,
            AmlScreeningRepository amlScreeningRepository,
            EnterpriseAuditService auditService,
            EventOutboxService eventOutboxService) {
        this.amlCaseRepository = amlCaseRepository;
        this.amlScreeningRepository = amlScreeningRepository;
        this.auditService = auditService;
        this.eventOutboxService = eventOutboxService;
    }

    @Transactional
    public AmlCaseResponse createCase(String tenantId, String actorId, CreateAmlCaseRequest request) {
        Instant now = Instant.now();
        String caseId = UUID.randomUUID().toString();

        AmlCaseEntity amlCase = new AmlCaseEntity();
        amlCase.setId(caseId);
        amlCase.setTenantId(tenantId);
        amlCase.setPartyId(request.partyId());
        amlCase.setCaseType(request.caseType());
        amlCase.setStatus(AmlCaseStatus.OPEN);
        amlCase.setPriority(request.priority());
        amlCase.setCreatedAt(now);
        amlCase.setUpdatedAt(now);
        amlCaseRepository.save(amlCase);

        auditService.record(tenantId, "AML_CASE", caseId, "CREATED", actorId,
                "caseType=" + request.caseType());
        eventOutboxService.publish(tenantId, "AML_CASE", caseId, "AML_CASE_CREATED",
                "partyId=" + request.partyId());

        return toResponse(amlCase);
    }

    @Transactional(readOnly = true)
    public AmlCaseResponse getCase(String tenantId, String caseId) {
        AmlCaseEntity amlCase = amlCaseRepository.findByIdAndTenantId(caseId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("AML case not found"));
        return toResponse(amlCase);
    }

    @Transactional(readOnly = true)
    public PageResponse<AmlCaseResponse> listCases(
            String tenantId, String partyId, AmlCaseStatus status, int page, int size) {
        Page<AmlCaseEntity> result = status == null
                ? amlCaseRepository.findByTenantIdAndPartyId(tenantId, partyId, PageRequest.of(page, size))
                : amlCaseRepository.findByTenantIdAndPartyIdAndStatus(tenantId, partyId, status, PageRequest.of(page, size));
        return new PageResponse<>(
                result.getContent().stream().map(AmlService::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), null);
    }

    @Transactional
    public AmlScreeningResponse runScreening(String tenantId, String actorId, AmlScreeningRequest request) {
        Instant now = Instant.now();
        String screeningId = UUID.randomUUID().toString();

        ScreeningResult result = ThreadLocalRandom.current().nextBoolean()
                ? ScreeningResult.CLEAR
                : ScreeningResult.REVIEW;

        AmlScreeningEntity screening = new AmlScreeningEntity();
        screening.setId(screeningId);
        screening.setTenantId(tenantId);
        screening.setPartyId(request.partyId());
        screening.setScreeningType(request.screeningType());
        screening.setResult(result);
        if (result == ScreeningResult.REVIEW) {
            screening.setMatchScore(ThreadLocalRandom.current().nextInt(60, 100));
        }
        screening.setCreatedAt(now);

        String caseId = null;
        if (result == ScreeningResult.REVIEW) {
            caseId = escalateToCase(tenantId, actorId, request.partyId(), request.screeningType(), now);
            screening.setCaseId(caseId);
        }

        amlScreeningRepository.save(screening);

        auditService.record(tenantId, "AML_SCREENING", screeningId, "COMPLETED", actorId,
                "result=" + result + ",screeningType=" + request.screeningType());
        eventOutboxService.publish(tenantId, "AML_SCREENING", screeningId, "AML_SCREENING_COMPLETED",
                "partyId=" + request.partyId() + ",result=" + result);

        return toResponse(screening);
    }

    private String escalateToCase(
            String tenantId, String actorId, String partyId, ScreeningType screeningType, Instant now) {
        String caseId = UUID.randomUUID().toString();
        AmlCaseType caseType = screeningType == ScreeningType.SANCTIONS
                ? AmlCaseType.SANCTIONS
                : AmlCaseType.PEP;

        AmlCaseEntity amlCase = new AmlCaseEntity();
        amlCase.setId(caseId);
        amlCase.setTenantId(tenantId);
        amlCase.setPartyId(partyId);
        amlCase.setCaseType(caseType);
        amlCase.setStatus(AmlCaseStatus.OPEN);
        amlCase.setPriority(AmlPriority.HIGH);
        amlCase.setCreatedAt(now);
        amlCase.setUpdatedAt(now);
        amlCaseRepository.save(amlCase);

        auditService.record(tenantId, "AML_CASE", caseId, "ESCALATED", actorId,
                "fromScreening=true,caseType=" + caseType);
        eventOutboxService.publish(tenantId, "AML_CASE", caseId, "AML_CASE_ESCALATED",
                "partyId=" + partyId);

        return caseId;
    }

    static AmlCaseResponse toResponse(AmlCaseEntity amlCase) {
        return new AmlCaseResponse(
                amlCase.getId(), amlCase.getPartyId(), amlCase.getCaseType(),
                amlCase.getStatus(), amlCase.getPriority(), amlCase.getAssignedTo(),
                amlCase.getCreatedAt(), amlCase.getUpdatedAt());
    }

    static AmlScreeningResponse toResponse(AmlScreeningEntity screening) {
        return new AmlScreeningResponse(
                screening.getId(), screening.getPartyId(), screening.getScreeningType(),
                screening.getResult(), screening.getMatchScore(), screening.getCaseId(),
                screening.getCreatedAt());
    }
}
