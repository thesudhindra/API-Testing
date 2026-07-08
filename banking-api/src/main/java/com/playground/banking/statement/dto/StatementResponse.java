package com.playground.banking.statement.dto;

import com.playground.banking.domain.StatementStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record StatementResponse(
        String id,
        String accountId,
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal openingBalance,
        BigDecimal closingBalance,
        String currency,
        StatementStatus status,
        Instant createdAt,
        List<StatementLineResponse> lines
) {
}
