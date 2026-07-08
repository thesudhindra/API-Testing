package com.playground.enterprise.loan.dto;

import com.playground.enterprise.domain.RepaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record RepaymentResponse(
        String id,
        String loanId,
        BigDecimal amount,
        String currency,
        RepaymentStatus status,
        Instant createdAt
) {
}
