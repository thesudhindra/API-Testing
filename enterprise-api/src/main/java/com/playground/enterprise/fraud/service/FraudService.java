package com.playground.enterprise.fraud.service;

import com.playground.enterprise.audit.service.EnterpriseAuditService;
import com.playground.enterprise.domain.FraudAlertStatus;
import com.playground.enterprise.event.service.EventOutboxService;
import com.playground.enterprise.fraud.dto.FraudAlertResponse;
import com.playground.enterprise.fraud.dto.FraudScreenRequest;
import com.playground.enterprise.fraud.dto.ReviewFraudAlertRequest;
import com.playground.enterprise.fraud.entity.FraudAlertEntity;
import com.playground.enterprise.fraud.repository.FraudAlertRepository;
import com.playground.common.dto.PageResponse;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class FraudService {

    private static final BigDecimal VELOCITY_THRESHOLD = new BigDecimal("5000");
    private static final int VELOCITY_RISK_SCORE = 85;
    private static final String VELOCITY_RULE_CODE = "VELOCITY";

    private final FraudAlertRepository fraudAlertRepository;
    private final EnterpriseAuditService auditService;
    private final EventOutboxService eventOutboxService;

    public FraudService(
            FraudAlertRepository fraudAlertRepository,
            EnterpriseAuditService auditService,
            EventOutboxService eventOutboxService) {
        this.fraudAlertRepository = fraudAlertRepository;
        this.auditService = auditService;
        this.eventOutboxService = eventOutboxService;
    }

    @Transactional
    public FraudAlertResponse screen(String tenantId, String actorId, FraudScreenRequest request) {
        if (request.amount().compareTo(VELOCITY_THRESHOLD) <= 0) {
            return null;
        }

        Instant now = Instant.now();
        String alertId = UUID.randomUUID().toString();

        FraudAlertEntity alert = new FraudAlertEntity();
        alert.setId(alertId);
        alert.setTenantId(tenantId);
        alert.setPartyId(request.partyId());
        alert.setEntityType(request.entityType());
        alert.setEntityId(request.entityId());
        alert.setRuleCode(VELOCITY_RULE_CODE);
        alert.setRiskScore(VELOCITY_RISK_SCORE);
        alert.setStatus(FraudAlertStatus.OPEN);
        alert.setDetails("Amount " + request.amount() + " exceeds velocity threshold");
        alert.setCreatedAt(now);
        fraudAlertRepository.save(alert);

        auditService.record(tenantId, "FRAUD_ALERT", alertId, "CREATED", actorId,
                "rule=" + VELOCITY_RULE_CODE + ",amount=" + request.amount());
        eventOutboxService.publish(tenantId, "FRAUD_ALERT", alertId, "FRAUD_ALERT_CREATED",
                "entityType=" + request.entityType() + ",entityId=" + request.entityId());

        return toResponse(alert);
    }

    @Transactional(readOnly = true)
    public PageResponse<FraudAlertResponse> listAlerts(
            String tenantId, String partyId, FraudAlertStatus status, int page, int size) {
        Page<FraudAlertEntity> result;
        if (partyId != null && status != null) {
            result = fraudAlertRepository.findByTenantIdAndPartyIdAndStatus(
                    tenantId, partyId, status, PageRequest.of(page, size));
        } else if (partyId != null) {
            result = fraudAlertRepository.findByTenantIdAndPartyId(tenantId, partyId, PageRequest.of(page, size));
        } else if (status != null) {
            result = fraudAlertRepository.findByTenantIdAndStatus(tenantId, status, PageRequest.of(page, size));
        } else {
            result = fraudAlertRepository.findByTenantId(tenantId, PageRequest.of(page, size));
        }
        return new PageResponse<>(
                result.getContent().stream().map(FraudService::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), null);
    }

    @Transactional
    public FraudAlertResponse reviewAlert(
            String tenantId, String actorId, String alertId, ReviewFraudAlertRequest request) {
        if (request.status() != FraudAlertStatus.REVIEWED && request.status() != FraudAlertStatus.DISMISSED) {
            throw new BadRequestException("Review status must be REVIEWED or DISMISSED");
        }

        FraudAlertEntity alert = fraudAlertRepository.findByIdAndTenantId(alertId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Fraud alert not found"));

        alert.setStatus(request.status());
        fraudAlertRepository.save(alert);

        auditService.record(tenantId, "FRAUD_ALERT", alertId, "REVIEWED", actorId,
                "status=" + request.status());

        return toResponse(alert);
    }

    static FraudAlertResponse toResponse(FraudAlertEntity alert) {
        return new FraudAlertResponse(
                alert.getId(), alert.getPartyId(), alert.getEntityType(), alert.getEntityId(),
                alert.getRuleCode(), alert.getRiskScore(), alert.getStatus(),
                alert.getDetails(), alert.getCreatedAt());
    }
}
