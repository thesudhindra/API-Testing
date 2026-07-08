package com.playground.enterprise.webhook.entity;

import com.playground.enterprise.domain.WebhookStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(schema = "enterprise", name = "webhooks")
public class WebhookEntity {

    @Id
    private String id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "event_type", nullable = false, length = 64)
    private String eventType;

    @Column(name = "target_url", nullable = false, length = 512)
    private String targetUrl;

    @Column(nullable = false, length = 128)
    private String secret;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }
    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public WebhookStatus getStatus() { return status; }
    public void setStatus(WebhookStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
