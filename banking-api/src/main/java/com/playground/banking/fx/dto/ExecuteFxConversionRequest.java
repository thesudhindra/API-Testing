package com.playground.banking.fx.dto;

import jakarta.validation.constraints.NotBlank;

public record ExecuteFxConversionRequest(
        @NotBlank String quoteId,
        @NotBlank String fromAccountId,
        @NotBlank String toAccountId,
        @NotBlank String partyId
) {
}
