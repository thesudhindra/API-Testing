package com.playground.banking.api;

import com.playground.banking.identity.dto.LoginRequest;
import com.playground.banking.identity.dto.TokenResponse;
import com.playground.banking.identity.dto.UserResponse;
import com.playground.banking.identity.service.AuthService;
import com.playground.common.security.TenantAccess;
import com.playground.common.api.ApiHeaders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Authentication", description = "JWT login and session identity")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtain access token", description = "Authenticate with tenant, username, and password.")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    public UserResponse me(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        return authService.currentUser(tenantId, jwt.getSubject());
    }
}
