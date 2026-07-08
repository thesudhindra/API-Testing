package com.playground.enterprise.webhook.dto;

import com.playground.enterprise.domain.DeliveryStatus;

import java.time.Instant;

public record WebhookDeliveryResponse(
        String id,
        String webhookId,
        String eventId,
        DeliveryStatus status,
        Integer httpStatus,
        int attemptCount,
        String lastError,
        Instant nextRetryAt,
        Instant createdAt,
        Instant completedAt
) {
}
