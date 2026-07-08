package com.playground.enterprise.loan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateRepaymentRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount
) {
}
