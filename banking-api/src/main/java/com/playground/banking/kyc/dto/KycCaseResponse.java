package com.playground.banking.kyc.dto;

import com.playground.banking.domain.KycStatus;

import java.time.Instant;

public record KycCaseResponse(
        String id,
        String partyId,
        KycStatus status,
        String level,
        String decisionReason,
        Instant createdAt,
        Instant updatedAt
) {
}
