package com.playground.banking.fx.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateFxQuoteRequest(
        @NotBlank @Size(max = 3) @Pattern(regexp = "[A-Z]{3}") String fromCurrency,
        @NotBlank @Size(max = 3) @Pattern(regexp = "[A-Z]{3}") String toCurrency,
        @NotNull @DecimalMin("0.01") BigDecimal fromAmount
) {
}
