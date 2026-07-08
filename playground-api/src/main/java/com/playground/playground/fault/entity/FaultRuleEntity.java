package com.playground.playground.fault.entity;

import com.playground.playground.domain.FaultTargetService;
import com.playground.playground.domain.FaultType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(schema = "playground", name = "fault_rules")
public class FaultRuleEntity {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_service", nullable = false, length = 32)
    private FaultTargetService targetService;

    @Column(name = "path_pattern", nullable = false, length = 256)
    private String pathPattern;

    @Enumerated(EnumType.STRING)
    @Column(name = "fault_type", nullable = false, length = 32)
    private FaultType faultType;

    @Column(name = "config_json", nullable = false, length = 1024)
    private String configJson;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FaultTargetService getTargetService() {
        return targetService;
    }

    public void setTargetService(FaultTargetService targetService) {
        this.targetService = targetService;
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public void setPathPattern(String pathPattern) {
        this.pathPattern = pathPattern;
    }

    public FaultType getFaultType() {
        return faultType;
    }

    public void setFaultType(FaultType faultType) {
        this.faultType = faultType;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
