package com.playground.enterprise.aml.entity;

import com.playground.enterprise.domain.AmlCaseStatus;
import com.playground.enterprise.domain.AmlCaseType;
import com.playground.enterprise.domain.AmlPriority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(schema = "enterprise", name = "aml_cases")
public class AmlCaseEntity {

    @Id
    private String id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "party_id", nullable = false)
    private String partyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_type", nullable = false, length = 32)
    private AmlCaseType caseType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AmlCaseStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AmlPriority priority;

    @Column(name = "assigned_to", length = 36)
    private String assignedTo;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getPartyId() { return partyId; }
    public void setPartyId(String partyId) { this.partyId = partyId; }
    public AmlCaseType getCaseType() { return caseType; }
    public void setCaseType(AmlCaseType caseType) { this.caseType = caseType; }
    public AmlCaseStatus getStatus() { return status; }
    public void setStatus(AmlCaseStatus status) { this.status = status; }
    public AmlPriority getPriority() { return priority; }
    public void setPriority(AmlPriority priority) { this.priority = priority; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
