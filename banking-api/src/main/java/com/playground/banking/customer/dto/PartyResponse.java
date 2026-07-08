package com.playground.banking.customer.dto;

import com.playground.banking.domain.PartyStatus;
import com.playground.banking.domain.PartyType;

import java.time.Instant;

public record PartyResponse(
        String id,
        String tenantId,
        PartyType partyType,
        PartyStatus status,
        String firstName,
        String lastName,
        String email,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
}
