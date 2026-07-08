package com.playground.banking.api;

import com.playground.banking.identity.dto.CreateUserRequest;
import com.playground.banking.identity.dto.UserResponse;
import com.playground.banking.identity.service.UserService;
import com.playground.common.security.TenantAccess;
import com.playground.common.api.ApiHeaders;
import com.playground.common.exception.ForbiddenException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@Tag(name = "Users", description = "Tenant-scoped user administration")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPS_AGENT', 'ADMIN')")
    @Operation(summary = "List users in tenant")
    public List<UserResponse> listUsers(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        return userService.listUsers(tenantId);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by id")
    public UserResponse getUser(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String userId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        if (!jwt.getSubject().equals(userId) && !TenantAccess.isPrivileged(jwt)) {
            throw new ForbiddenException("Cannot view another user's profile");
        }
        return userService.getUser(tenantId, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user")
    public UserResponse createUser(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody CreateUserRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        return userService.createUser(tenantId, request);
    }
}
