package com.playground.enterprise.aml.entity;

import com.playground.enterprise.domain.ScreeningResult;
import com.playground.enterprise.domain.ScreeningType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(schema = "enterprise", name = "aml_screenings")
public class AmlScreeningEntity {

    @Id
    private String id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "party_id", nullable = false)
    private String partyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "screening_type", nullable = false, length = 32)
    private ScreeningType screeningType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ScreeningResult result;

    @Column(name = "match_score")
    private Integer matchScore;

    @Column(name = "case_id", length = 36)
    private String caseId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getPartyId() { return partyId; }
    public void setPartyId(String partyId) { this.partyId = partyId; }
    public ScreeningType getScreeningType() { return screeningType; }
    public void setScreeningType(ScreeningType screeningType) { this.screeningType = screeningType; }
    public ScreeningResult getResult() { return result; }
    public void setResult(ScreeningResult result) { this.result = result; }
    public Integer getMatchScore() { return matchScore; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }
    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
