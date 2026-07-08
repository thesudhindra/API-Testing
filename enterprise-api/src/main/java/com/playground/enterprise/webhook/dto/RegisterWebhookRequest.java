package com.playground.enterprise.webhook.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterWebhookRequest(
        @NotBlank String eventType,
        @NotBlank String targetUrl,
        String secret
) {
}
