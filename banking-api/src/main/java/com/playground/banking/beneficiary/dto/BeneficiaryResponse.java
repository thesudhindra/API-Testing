package com.playground.banking.beneficiary.dto;

import com.playground.banking.domain.BeneficiaryStatus;

import java.time.Instant;

public record BeneficiaryResponse(
        String id,
        String partyId,
        String nickname,
        String sortCode,
        String accountNumber,
        String iban,
        BeneficiaryStatus status,
        Instant verifiedAt,
        Instant firstPaymentAllowedAfter,
        Instant createdAt
) {
}
