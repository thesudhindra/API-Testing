package com.playground.enterprise.audit.dto;

import java.time.Instant;

public record AuditEventResponse(
        String id,
        String tenantId,
        String entityType,
        String entityId,
        String action,
        String actorId,
        String correlationId,
        String details,
        String prevHash,
        String eventHash,
        Instant createdAt
) {
}
