package com.playground.banking.identity.dto;

import java.util.List;

public record UserResponse(
        String id,
        String tenantId,
        String username,
        String partyId,
        boolean enabled,
        List<String> roles
) {
}
