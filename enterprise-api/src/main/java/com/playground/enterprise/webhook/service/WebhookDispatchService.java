package com.playground.enterprise.webhook.service;

import com.playground.enterprise.domain.DeliveryStatus;
import com.playground.enterprise.domain.WebhookStatus;
import com.playground.enterprise.webhook.entity.WebhookDeliveryEntity;
import com.playground.enterprise.webhook.entity.WebhookEntity;
import com.playground.enterprise.webhook.repository.WebhookDeliveryRepository;
import com.playground.enterprise.webhook.repository.WebhookRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class WebhookDispatchService {

    private final WebhookRepository webhookRepository;
    private final WebhookDeliveryRepository webhookDeliveryRepository;

    public WebhookDispatchService(
            WebhookRepository webhookRepository,
            WebhookDeliveryRepository webhookDeliveryRepository) {
        this.webhookRepository = webhookRepository;
        this.webhookDeliveryRepository = webhookDeliveryRepository;
    }

    @Transactional
    public void enqueueForEvent(String tenantId, String eventId, String eventType, String payloadJson) {
        List<WebhookEntity> webhooks = webhookRepository.findByTenantId(tenantId, Pageable.unpaged())
                .getContent().stream()
                .filter(webhook -> webhook.getStatus() == WebhookStatus.ACTIVE)
                .filter(webhook -> webhook.getEventType().equals(eventType))
                .toList();

        Instant now = Instant.now();
        for (WebhookEntity webhook : webhooks) {
            WebhookDeliveryEntity delivery = new WebhookDeliveryEntity();
            delivery.setId(UUID.randomUUID().toString());
            delivery.setTenantId(tenantId);
            delivery.setWebhookId(webhook.getId());
            delivery.setEventId(eventId);
            delivery.setStatus(DeliveryStatus.PENDING);
            delivery.setAttemptCount(0);
            delivery.setNextRetryAt(now);
            delivery.setCreatedAt(now);
            webhookDeliveryRepository.save(delivery);
        }
    }
}
