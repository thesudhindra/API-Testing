package com.playground.enterprise.webhook.service;

import com.playground.enterprise.domain.WebhookStatus;
import com.playground.common.dto.PageResponse;
import com.playground.enterprise.webhook.dto.RegisterWebhookRequest;
import com.playground.enterprise.webhook.dto.WebhookDeliveryResponse;
import com.playground.enterprise.webhook.dto.WebhookResponse;
import com.playground.enterprise.webhook.entity.WebhookDeliveryEntity;
import com.playground.enterprise.webhook.entity.WebhookEntity;
import com.playground.enterprise.webhook.repository.WebhookDeliveryRepository;
import com.playground.enterprise.webhook.repository.WebhookRepository;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class WebhookService {

    private final WebhookRepository webhookRepository;
    private final WebhookDeliveryRepository webhookDeliveryRepository;

    public WebhookService(
            WebhookRepository webhookRepository,
            WebhookDeliveryRepository webhookDeliveryRepository) {
        this.webhookRepository = webhookRepository;
        this.webhookDeliveryRepository = webhookDeliveryRepository;
    }

    @Transactional
    public WebhookResponse register(String tenantId, String eventType, String targetUrl, String secret) {
        WebhookEntity webhook = new WebhookEntity();
        webhook.setId(UUID.randomUUID().toString());
        webhook.setTenantId(tenantId);
        webhook.setEventType(eventType);
        webhook.setTargetUrl(targetUrl);
        webhook.setSecret(secret);
        webhook.setStatus(WebhookStatus.ACTIVE);
        webhook.setCreatedAt(Instant.now());

        return toResponse(webhookRepository.save(webhook));
    }

    @Transactional
    public WebhookResponse register(String tenantId, RegisterWebhookRequest request) {
        return register(tenantId, request.eventType(), request.targetUrl(), request.secret());
    }

    @Transactional(readOnly = true)
    public PageResponse<WebhookResponse> list(String tenantId) {
        Page<WebhookEntity> result = webhookRepository.findByTenantId(tenantId, PageRequest.of(0, 100));
        return new PageResponse<>(
                result.getContent().stream().map(WebhookService::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                null);
    }

    @Transactional(readOnly = true)
    public PageResponse<WebhookDeliveryResponse> listDeliveries(
            String tenantId, String webhookId, int page, int size) {
        webhookRepository.findById(webhookId)
                .filter(webhook -> tenantId.equals(webhook.getTenantId()))
                .orElseThrow(() -> new ResourceNotFoundException("Webhook not found"));

        Page<WebhookDeliveryEntity> result = webhookDeliveryRepository.findByTenantIdAndWebhookId(
                tenantId, webhookId, PageRequest.of(page, size));
        return new PageResponse<>(
                result.getContent().stream().map(WebhookService::toDeliveryResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                null);
    }

    static WebhookDeliveryResponse toDeliveryResponse(WebhookDeliveryEntity delivery) {
        return new WebhookDeliveryResponse(
                delivery.getId(),
                delivery.getWebhookId(),
                delivery.getEventId(),
                delivery.getStatus(),
                delivery.getHttpStatus(),
                delivery.getAttemptCount(),
                delivery.getLastError(),
                delivery.getNextRetryAt(),
                delivery.getCreatedAt(),
                delivery.getCompletedAt());
    }

    static WebhookResponse toResponse(WebhookEntity webhook) {
        return new WebhookResponse(
                webhook.getId(),
                webhook.getTenantId(),
                webhook.getEventType(),
                webhook.getTargetUrl(),
                webhook.getStatus(),
                webhook.getCreatedAt());
    }
}
