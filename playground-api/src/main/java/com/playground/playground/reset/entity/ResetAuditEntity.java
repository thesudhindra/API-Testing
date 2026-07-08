package com.playground.playground.reset.entity;

import com.playground.playground.domain.ResetScope;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(schema = "playground", name = "reset_audit")
public class ResetAuditEntity {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ResetScope scope;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(length = 1024)
    private String details;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ResetScope getScope() {
        return scope;
    }

    public void setScope(ResetScope scope) {
        this.scope = scope;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
