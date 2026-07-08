package com.playground.enterprise.deposit.fixed.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateFixedDepositRequest(
        @NotBlank String partyId,
        @NotBlank String accountId,
        @NotNull @DecimalMin("0.01") BigDecimal principal,
        @NotBlank @Size(max = 3) @Pattern(regexp = "[A-Z]{3}") String currency,
        @NotNull @DecimalMin("0") BigDecimal interestRate,
        @Min(1) int termDays
) {
}
