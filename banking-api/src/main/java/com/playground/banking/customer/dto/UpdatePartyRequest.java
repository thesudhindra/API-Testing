package com.playground.banking.customer.dto;

import com.playground.banking.domain.PartyStatus;
import com.playground.banking.domain.PartyType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdatePartyRequest(
        PartyType partyType,
        PartyStatus status,
        @Size(max = 128) String firstName,
        @Size(max = 128) String lastName,
        @Email @Size(max = 256) String email
) {
}
