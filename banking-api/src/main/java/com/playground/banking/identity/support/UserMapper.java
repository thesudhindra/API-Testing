package com.playground.banking.identity.support;

import com.playground.banking.identity.dto.UserResponse;
import com.playground.banking.identity.entity.RoleEntity;
import com.playground.banking.identity.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(UserEntity user) {
        List<String> roles = user.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toList());
        return new UserResponse(
                user.getId(),
                user.getTenantId(),
                user.getUsername(),
                user.getPartyId(),
                user.isEnabled(),
                roles);
    }
}
