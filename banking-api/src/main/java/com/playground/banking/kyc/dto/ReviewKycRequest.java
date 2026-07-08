package com.playground.banking.kyc.dto;

import com.playground.banking.domain.KycStatus;
import jakarta.validation.constraints.NotNull;

public record ReviewKycRequest(
        @NotNull KycStatus status,
        String decisionReason
) {
}
