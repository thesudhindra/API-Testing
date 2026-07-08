package com.playground.banking.transaction.dto;

import com.playground.banking.domain.TransactionStatus;
import com.playground.banking.domain.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        String id,
        String partyId,
        String accountId,
        TransactionType txnType,
        TransactionStatus status,
        BigDecimal amount,
        String currency,
        String reference,
        String description,
        Instant createdAt
) {
}
