package com.playground.banking.audit.service;

import com.playground.banking.audit.entity.AuditEventEntity;
import com.playground.banking.audit.repository.AuditEventRepository;
import com.playground.common.context.CorrelationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void record(String tenantId, String entityType, String entityId, String action, String actorId, String details) {
        AuditEventEntity event = new AuditEventEntity();
        event.setId(UUID.randomUUID().toString());
        event.setTenantId(tenantId);
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        event.setAction(action);
        event.setActorId(actorId);
        event.setCorrelationId(CorrelationContext.getOrGenerate());
        event.setDetails(details);
        event.setCreatedAt(Instant.now());
        auditEventRepository.save(event);
    }
}
