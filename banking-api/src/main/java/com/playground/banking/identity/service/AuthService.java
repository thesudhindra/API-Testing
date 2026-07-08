package com.playground.banking.identity.service;

import com.playground.banking.identity.dto.LoginRequest;
import com.playground.banking.identity.dto.TokenResponse;
import com.playground.banking.identity.dto.UserResponse;
import com.playground.banking.identity.entity.UserEntity;
import com.playground.banking.identity.repository.UserRepository;
import com.playground.banking.identity.support.UserMapper;
import com.playground.banking.security.JwtTokenService;
import com.playground.common.exception.ResourceNotFoundException;
import com.playground.common.exception.UnauthorizedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByTenantIdAndUsername(request.tenantId(), request.username())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.isEnabled() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtTokenService.createToken(user);
        return new TokenResponse(token, "Bearer", jwtTokenService.getTtlSeconds());
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser(String tenantId, String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!user.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return UserMapper.toResponse(user);
    }
}
