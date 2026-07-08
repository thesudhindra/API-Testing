package com.playground.enterprise.event.dto;

import com.playground.enterprise.domain.EventStatus;

import java.time.Instant;

public record EventResponse(
        String id,
        String tenantId,
        String aggregateType,
        String aggregateId,
        String eventType,
        String payload,
        EventStatus status,
        Instant publishedAt,
        Instant createdAt
) {
}
