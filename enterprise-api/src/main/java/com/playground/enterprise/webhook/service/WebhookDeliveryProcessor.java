package com.playground.enterprise.webhook.service;

import com.playground.enterprise.domain.CircuitBreakerState;
import com.playground.enterprise.domain.DeliveryStatus;
import com.playground.enterprise.resilience.service.CircuitBreakerService;
import com.playground.enterprise.resilience.service.RetryExecutor;
import com.playground.enterprise.webhook.client.WebhookHttpClient;
import com.playground.enterprise.webhook.entity.WebhookDeliveryEntity;
import com.playground.enterprise.webhook.entity.WebhookEntity;
import com.playground.enterprise.webhook.repository.WebhookDeliveryRepository;
import com.playground.enterprise.webhook.repository.WebhookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class WebhookDeliveryProcessor {

    private static final Logger log = LoggerFactory.getLogger(WebhookDeliveryProcessor.class);
    private static final String CIRCUIT_BREAKER_NAME = "webhook-delivery";

    private final WebhookDeliveryRepository webhookDeliveryRepository;
    private final WebhookRepository webhookRepository;
    private final WebhookHttpClient webhookHttpClient;
    private final CircuitBreakerService circuitBreakerService;
    private final RetryExecutor retryExecutor;

    @Value("${playground.enterprise.webhooks.max-retries:3}")
    private int maxRetries;

    @Value("${playground.enterprise.webhooks.retry-backoff-ms:1000}")
    private long retryBackoffMs;

    public WebhookDeliveryProcessor(
            WebhookDeliveryRepository webhookDeliveryRepository,
            WebhookRepository webhookRepository,
            WebhookHttpClient webhookHttpClient,
            CircuitBreakerService circuitBreakerService,
            RetryExecutor retryExecutor) {
        this.webhookDeliveryRepository = webhookDeliveryRepository;
        this.webhookRepository = webhookRepository;
        this.webhookHttpClient = webhookHttpClient;
        this.circuitBreakerService = circuitBreakerService;
        this.retryExecutor = retryExecutor;
    }

    @Scheduled(fixedDelayString = "${playground.enterprise.webhooks.poll-interval-ms:2000}")
    public void processDeliveries() {
        List<WebhookDeliveryEntity> due = new ArrayList<>();
        due.addAll(webhookDeliveryRepository.findByStatusAndNextRetryAtBefore(
                DeliveryStatus.PENDING, Instant.now().plusSeconds(1)));

        for (WebhookDeliveryEntity delivery : due) {
            processDelivery(delivery.getId());
        }
    }

    @Transactional
    public void processDelivery(String deliveryId) {
        WebhookDeliveryEntity delivery = webhookDeliveryRepository.findById(deliveryId).orElse(null);
        if (delivery == null || delivery.getStatus() != DeliveryStatus.PENDING) {
            return;
        }

        CircuitBreakerState state = circuitBreakerService.getState(CIRCUIT_BREAKER_NAME);
        if (state == CircuitBreakerState.OPEN) {
            log.debug("Circuit breaker open; skipping delivery {}", deliveryId);
            return;
        }

        WebhookEntity webhook = webhookRepository.findByIdAndTenantId(delivery.getWebhookId(), delivery.getTenantId())
                .orElse(null);
        if (webhook == null) {
            delivery.setStatus(DeliveryStatus.FAILED);
            delivery.setLastError("Webhook not found");
            delivery.setCompletedAt(Instant.now());
            webhookDeliveryRepository.save(delivery);
            return;
        }

        String payload = buildPayload(delivery);
        try {
            retryExecutor.executeWithRetry(
                    () -> deliver(webhook, payload),
                    0,
                    retryBackoffMs);

            delivery.setStatus(DeliveryStatus.DELIVERED);
            delivery.setHttpStatus(200);
            delivery.setCompletedAt(Instant.now());
            webhookDeliveryRepository.save(delivery);
            circuitBreakerService.recordSuccess(CIRCUIT_BREAKER_NAME);
        } catch (Exception e) {
            handleDeliveryFailure(delivery, e.getMessage());
        }
    }

    private void deliver(WebhookEntity webhook, String payload) {
        int status = webhookHttpClient.post(webhook.getTargetUrl(), payload, webhook.getSecret());
        if (status < 200 || status >= 300) {
            throw new WebhookHttpClient.WebhookDeliveryException("HTTP " + status);
        }
    }

    private void handleDeliveryFailure(WebhookDeliveryEntity delivery, String error) {
        log.warn("Webhook delivery {} failed: {}", delivery.getId(), error);
        circuitBreakerService.recordFailure(CIRCUIT_BREAKER_NAME);

        delivery.setAttemptCount(delivery.getAttemptCount() + 1);
        delivery.setLastError(error);

        if (delivery.getAttemptCount() >= maxRetries) {
            delivery.setStatus(DeliveryStatus.DLQ);
            delivery.setCompletedAt(Instant.now());
        } else {
            delivery.setNextRetryAt(Instant.now().plusMillis(retryBackoffMs * delivery.getAttemptCount()));
        }

        webhookDeliveryRepository.save(delivery);
    }

    private static String buildPayload(WebhookDeliveryEntity delivery) {
        return "{\"eventId\":\"" + delivery.getEventId() + "\",\"deliveryId\":\"" + delivery.getId() + "\"}";
    }
}
