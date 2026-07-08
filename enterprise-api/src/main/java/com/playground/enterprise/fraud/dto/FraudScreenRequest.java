package com.playground.enterprise.fraud.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FraudScreenRequest(
        @NotBlank String entityType,
        @NotBlank String entityId,
        String partyId,
        @NotNull @DecimalMin("0") BigDecimal amount
) {
}
