package com.playground.banking.fx.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record FxQuoteResponse(
        String id,
        String fromCurrency,
        String toCurrency,
        BigDecimal rate,
        BigDecimal fromAmount,
        BigDecimal toAmount,
        Instant expiresAt,
        Instant createdAt
) {
}
