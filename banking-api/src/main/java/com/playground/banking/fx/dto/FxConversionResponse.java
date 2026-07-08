package com.playground.banking.fx.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record FxConversionResponse(
        String id,
        String transactionId,
        String quoteId,
        String fromAccountId,
        String toAccountId,
        BigDecimal fromAmount,
        BigDecimal toAmount,
        String fromCurrency,
        String toCurrency,
        BigDecimal rate,
        Instant createdAt
) {
}
