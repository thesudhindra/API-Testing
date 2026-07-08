package com.playground.enterprise.webhook.repository;

import com.playground.enterprise.domain.WebhookStatus;
import com.playground.enterprise.webhook.entity.WebhookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WebhookRepository extends JpaRepository<WebhookEntity, String> {

    Optional<WebhookEntity> findByIdAndTenantId(String id, String tenantId);

    Page<WebhookEntity> findByTenantId(String tenantId, Pageable pageable);

    Page<WebhookEntity> findByTenantIdAndStatus(String tenantId, WebhookStatus status, Pageable pageable);
}
