package com.playground.enterprise.loan.dto;

import com.playground.enterprise.domain.LoanStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record LoanResponse(
        String id,
        String partyId,
        String accountId,
        String productCode,
        BigDecimal principal,
        String currency,
        BigDecimal interestRate,
        int termMonths,
        LoanStatus status,
        BigDecimal outstandingBalance,
        Instant createdAt,
        Instant updatedAt
) {
}
