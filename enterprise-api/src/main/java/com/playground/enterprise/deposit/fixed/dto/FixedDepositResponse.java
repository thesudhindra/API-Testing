package com.playground.enterprise.deposit.fixed.dto;

import com.playground.enterprise.domain.DepositStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record FixedDepositResponse(
        String id,
        String partyId,
        String accountId,
        BigDecimal principal,
        String currency,
        BigDecimal interestRate,
        int termDays,
        LocalDate maturityDate,
        DepositStatus status,
        Instant createdAt
) {
}
