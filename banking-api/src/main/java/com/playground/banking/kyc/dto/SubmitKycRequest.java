package com.playground.banking.kyc.dto;

import com.playground.banking.domain.KycStatus;
import jakarta.validation.constraints.NotBlank;

public record SubmitKycRequest(
        @NotBlank String partyId
) {
}
