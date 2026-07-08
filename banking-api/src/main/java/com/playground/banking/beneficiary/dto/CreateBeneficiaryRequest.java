package com.playground.banking.beneficiary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBeneficiaryRequest(
        @NotBlank String partyId,
        @NotBlank @Size(max = 128) String nickname,
        @Size(max = 16) String sortCode,
        @Size(max = 34) String accountNumber,
        @Size(max = 34) String iban
) {
}
