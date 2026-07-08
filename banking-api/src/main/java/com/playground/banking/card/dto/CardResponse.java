package com.playground.banking.card.dto;

import com.playground.banking.domain.CardStatus;

import java.time.Instant;

public record CardResponse(
        String id,
        String partyId,
        String accountId,
        String panLast4,
        String productCode,
        CardStatus status,
        Instant createdAt
) {
}
