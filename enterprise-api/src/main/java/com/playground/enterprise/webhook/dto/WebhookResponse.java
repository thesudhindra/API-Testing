package com.playground.enterprise.webhook.dto;

import com.playground.enterprise.domain.WebhookStatus;

import java.time.Instant;

public record WebhookResponse(
        String id,
        String tenantId,
        String eventType,
        String targetUrl,
        WebhookStatus status,
        Instant createdAt
) {
}
