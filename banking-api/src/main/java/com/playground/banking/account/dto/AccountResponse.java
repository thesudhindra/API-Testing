package com.playground.banking.account.dto;

import com.playground.banking.domain.AccountStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        String id,
        String tenantId,
        String partyId,
        String accountNumber,
        String currency,
        String productCode,
        AccountStatus status,
        BigDecimal availableBalance,
        BigDecimal ledgerBalance,
        long version,
        Instant createdAt
) {
}
