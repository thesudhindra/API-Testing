package com.playground.banking.payment.dto;

import com.playground.banking.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        String id,
        String transactionId,
        String partyId,
        String accountId,
        String beneficiaryId,
        BigDecimal amount,
        String currency,
        String reference,
        PaymentStatus status,
        Instant createdAt
) {
}
