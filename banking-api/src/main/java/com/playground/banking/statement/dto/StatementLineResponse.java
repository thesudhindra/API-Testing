package com.playground.banking.statement.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record StatementLineResponse(
        String id,
        String transactionId,
        Instant postedAt,
        String description,
        BigDecimal amount,
        BigDecimal balanceAfter,
        int sortOrder
) {
}
