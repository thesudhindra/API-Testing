package com.playground.enterprise.webhook.repository;

import com.playground.enterprise.domain.DeliveryStatus;
import com.playground.enterprise.webhook.entity.WebhookDeliveryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WebhookDeliveryRepository extends JpaRepository<WebhookDeliveryEntity, String> {

    Optional<WebhookDeliveryEntity> findByIdAndTenantId(String id, String tenantId);

    List<WebhookDeliveryEntity> findByStatusAndNextRetryAtBefore(DeliveryStatus status, Instant nextRetryAt);

    Page<WebhookDeliveryEntity> findByTenantIdAndWebhookId(
            String tenantId, String webhookId, Pageable pageable);
}
