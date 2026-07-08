package com.playground.banking.transfer.dto;

import com.playground.banking.domain.TransferStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferResponse(
        String id,
        String transactionId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        String currency,
        String reference,
        TransferStatus status,
        Instant createdAt
) {
}
