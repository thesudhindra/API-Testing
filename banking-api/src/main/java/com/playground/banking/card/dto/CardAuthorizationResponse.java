package com.playground.banking.card.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record CardAuthorizationResponse(
        String id,
        String cardId,
        String transactionId,
        String merchantName,
        BigDecimal amount,
        String currency,
        String status,
        Instant createdAt
) {
}
