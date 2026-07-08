package com.playground.banking.identity.service;

import com.playground.banking.identity.dto.CreateUserRequest;
import com.playground.banking.identity.dto.UserResponse;
import com.playground.banking.identity.entity.RoleEntity;
import com.playground.banking.identity.entity.UserEntity;
import com.playground.banking.identity.repository.RoleRepository;
import com.playground.banking.identity.repository.UserRepository;
import com.playground.banking.identity.support.UserMapper;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ConflictException;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers(String tenantId) {
        return userRepository.findByTenantId(tenantId).stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(String tenantId, String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!user.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return UserMapper.toResponse(user);
    }

    @Transactional
    public UserResponse createUser(String tenantId, CreateUserRequest request) {
        if (userRepository.existsByTenantIdAndUsername(tenantId, request.username())) {
            throw new ConflictException("Username already exists in tenant");
        }

        Set<RoleEntity> roles = resolveRoles(tenantId, request.roleNames());
        UserEntity user = new UserEntity();
        user.setId("user-" + UUID.randomUUID());
        user.setTenantId(tenantId);
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPartyId(request.partyId());
        user.setEnabled(true);
        user.setCreatedAt(Instant.now());
        user.setRoles(roles);

        return UserMapper.toResponse(userRepository.save(user));
    }

    private Set<RoleEntity> resolveRoles(String tenantId, Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            throw new BadRequestException("At least one role is required");
        }
        Set<RoleEntity> roles = new HashSet<>();
        for (String name : roleNames) {
            RoleEntity role = roleRepository.findByTenantIdAndName(tenantId, name)
                    .orElseThrow(() -> new BadRequestException("Unknown role: " + name));
            roles.add(role);
        }
        return roles;
    }
}
