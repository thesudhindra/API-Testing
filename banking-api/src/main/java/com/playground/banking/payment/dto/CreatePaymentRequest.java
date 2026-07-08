package com.playground.banking.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotBlank String partyId,
        @NotBlank String accountId,
        @NotBlank String beneficiaryId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @Size(max = 3) @Pattern(regexp = "[A-Z]{3}") String currency,
        @Size(max = 128) String reference
) {
}
