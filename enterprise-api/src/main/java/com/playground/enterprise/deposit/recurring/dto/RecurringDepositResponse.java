package com.playground.enterprise.deposit.recurring.dto;

import com.playground.enterprise.domain.DepositFrequency;
import com.playground.enterprise.domain.DepositStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record RecurringDepositResponse(
        String id,
        String partyId,
        String accountId,
        BigDecimal installmentAmount,
        String currency,
        DepositFrequency frequency,
        DepositStatus status,
        LocalDate nextDueDate,
        Instant createdAt
) {
}
