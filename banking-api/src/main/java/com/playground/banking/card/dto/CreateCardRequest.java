package com.playground.banking.card.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCardRequest(
        @NotBlank String partyId,
        @NotBlank String accountId,
        @NotBlank @Size(max = 32) String productCode
) {
}
