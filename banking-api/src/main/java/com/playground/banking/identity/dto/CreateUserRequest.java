package com.playground.banking.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateUserRequest(
        @NotBlank @Size(max = 128) String username,
        @NotBlank @Size(min = 8, max = 128) String password,
        String partyId,
        @NotBlank Set<String> roleNames
) {
}
