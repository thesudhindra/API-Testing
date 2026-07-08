package com.playground.banking.identity.service;

import com.playground.banking.identity.dto.LoginRequest;
import com.playground.banking.identity.entity.RoleEntity;
import com.playground.banking.identity.entity.UserEntity;
import com.playground.banking.identity.repository.UserRepository;
import com.playground.banking.security.JwtTokenService;
import com.playground.common.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginReturnsTokenForValidCredentials() {
        UserEntity user = userWithPassword("hash");
        when(userRepository.findByTenantIdAndUsername("tenant-demo", "customer")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hash")).thenReturn(true);
        when(jwtTokenService.createToken(user)).thenReturn("jwt-token");
        when(jwtTokenService.getTtlSeconds()).thenReturn(3600L);

        var response = authService.login(new LoginRequest("tenant-demo", "customer", "password"));

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600L);
        verify(jwtTokenService).createToken(user);
    }

    @Test
    void loginRejectsInvalidPassword() {
        UserEntity user = userWithPassword("hash");
        when(userRepository.findByTenantIdAndUsername("tenant-demo", "customer")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("tenant-demo", "customer", "wrong")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void loginRejectsUnknownUser() {
        when(userRepository.findByTenantIdAndUsername(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("tenant-demo", "nobody", "password")))
                .isInstanceOf(UnauthorizedException.class);
    }

    private static UserEntity userWithPassword(String hash) {
        UserEntity user = new UserEntity();
        user.setId("user-customer");
        user.setTenantId("tenant-demo");
        user.setUsername("customer");
        user.setPasswordHash(hash);
        user.setEnabled(true);
        RoleEntity role = new RoleEntity();
        role.setName("RETAIL_CUSTOMER");
        user.setRoles(Set.of(role));
        return user;
    }
}
