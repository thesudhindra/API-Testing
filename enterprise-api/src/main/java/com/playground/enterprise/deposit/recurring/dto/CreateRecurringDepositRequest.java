package com.playground.enterprise.deposit.recurring.dto;

import com.playground.enterprise.domain.DepositFrequency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateRecurringDepositRequest(
        @NotBlank String partyId,
        @NotBlank String accountId,
        @NotNull @DecimalMin("0.01") BigDecimal installmentAmount,
        @NotBlank @Size(max = 3) @Pattern(regexp = "[A-Z]{3}") String currency,
        @NotNull DepositFrequency frequency
) {
}
