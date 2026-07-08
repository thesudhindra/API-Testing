package com.playground.enterprise.audit.service;

import com.playground.enterprise.audit.dto.AuditEventResponse;
import com.playground.enterprise.audit.entity.AuditEventEntity;
import com.playground.enterprise.audit.repository.AuditEventRepository;
import com.playground.common.dto.PageResponse;
import com.playground.common.context.CorrelationContext;
import com.playground.common.util.DigestSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class EnterpriseAuditService {

    private static final String GENESIS_HASH = "0".repeat(64);

    private final AuditEventRepository auditEventRepository;

    public EnterpriseAuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void record(
            String tenantId,
            String entityType,
            String entityId,
            String action,
            String actorId,
            String details) {
        String correlationId = CorrelationContext.getOrGenerate();
        String prevHash = auditEventRepository
                .findTopByTenantIdOrderByCreatedAtDesc(tenantId)
                .map(AuditEventEntity::getEventHash)
                .orElse(GENESIS_HASH);

        String payload = tenantId + "|" + entityType + "|" + entityId + "|" + action + "|"
                + nullToEmpty(actorId) + "|" + nullToEmpty(details) + "|" + correlationId;
        String eventHash = DigestSupport.sha256(prevHash + payload);

        AuditEventEntity event = new AuditEventEntity();
        event.setId(UUID.randomUUID().toString());
        event.setTenantId(tenantId);
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        event.setAction(action);
        event.setActorId(actorId);
        event.setCorrelationId(correlationId);
        event.setDetails(details);
        event.setPrevHash(prevHash);
        event.setEventHash(eventHash);
        event.setCreatedAt(Instant.now());

        auditEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditEventResponse> query(
            String tenantId,
            String entityType,
            String correlationId,
            Instant from,
            Instant to,
            int page,
            int size) {
        Instant fromInstant = from != null ? from : Instant.EPOCH;
        Instant toInstant = to != null ? to : Instant.now();

        Page<AuditEventEntity> result = auditEventRepository.search(
                tenantId, entityType, correlationId, fromInstant, toInstant,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        return new PageResponse<>(
                result.getContent().stream().map(EnterpriseAuditService::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                null);
    }

    private static AuditEventResponse toResponse(AuditEventEntity event) {
        return new AuditEventResponse(
                event.getId(),
                event.getTenantId(),
                event.getEntityType(),
                event.getEntityId(),
                event.getAction(),
                event.getActorId(),
                event.getCorrelationId(),
                event.getDetails(),
                event.getPrevHash(),
                event.getEventHash(),
                event.getCreatedAt());
    }

    private static String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
