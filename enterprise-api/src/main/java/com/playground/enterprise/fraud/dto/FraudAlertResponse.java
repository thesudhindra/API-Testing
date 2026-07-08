package com.playground.enterprise.fraud.dto;

import com.playground.enterprise.domain.FraudAlertStatus;

import java.time.Instant;

public record FraudAlertResponse(
        String id,
        String partyId,
        String entityType,
        String entityId,
        String ruleCode,
        int riskScore,
        FraudAlertStatus status,
        String details,
        Instant createdAt
) {
}
