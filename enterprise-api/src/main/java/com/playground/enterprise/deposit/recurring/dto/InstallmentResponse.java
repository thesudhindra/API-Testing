package com.playground.enterprise.deposit.recurring.dto;

import com.playground.enterprise.domain.InstallmentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record InstallmentResponse(
        String id,
        String recurringDepositId,
        BigDecimal amount,
        InstallmentStatus status,
        LocalDate dueDate,
        Instant paidAt,
        Instant createdAt
) {
}
