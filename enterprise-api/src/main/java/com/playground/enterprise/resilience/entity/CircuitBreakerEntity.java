package com.playground.enterprise.resilience.entity;

import com.playground.enterprise.domain.CircuitBreakerState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(schema = "enterprise", name = "circuit_breakers")
public class CircuitBreakerEntity {

    @Id
    @Column(length = 64)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CircuitBreakerState state;

    @Column(name = "failure_count", nullable = false)
    private int failureCount;

    @Column(name = "success_count", nullable = false)
    private int successCount;

    @Column(name = "opened_at")
    private Instant openedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public CircuitBreakerState getState() { return state; }
    public void setState(CircuitBreakerState state) { this.state = state; }
    public int getFailureCount() { return failureCount; }
    public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
    public Instant getOpenedAt() { return openedAt; }
    public void setOpenedAt(Instant openedAt) { this.openedAt = openedAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
