package com.playground.banking.ledger.dto;

import com.playground.banking.domain.LedgerEntryType;

import java.math.BigDecimal;
import java.time.Instant;

public record LedgerEntryResponse(
        String id,
        String transactionId,
        String accountId,
        LedgerEntryType entryType,
        BigDecimal amount,
        String currency,
        BigDecimal balanceAfter,
        Instant createdAt
) {
}
