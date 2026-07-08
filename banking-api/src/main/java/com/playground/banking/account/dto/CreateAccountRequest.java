package com.playground.banking.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
        @NotBlank String partyId,
        @NotBlank @Size(max = 3) @Pattern(regexp = "[A-Z]{3}") String currency,
        @NotBlank @Size(max = 32) String productCode
) {
}
