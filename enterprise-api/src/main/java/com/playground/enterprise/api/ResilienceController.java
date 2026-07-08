package com.playground.enterprise.api;

import com.playground.enterprise.resilience.dto.CircuitBreakerResponse;
import com.playground.enterprise.resilience.service.CircuitBreakerService;
import com.playground.common.security.TenantAccess;
import com.playground.common.api.ApiHeaders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/resilience/circuit-breakers")
@Tag(name = "Resilience", description = "Circuit breaker status")
@SecurityRequirement(name = "bearerAuth")
public class ResilienceController {

    private final CircuitBreakerService circuitBreakerService;

    public ResilienceController(CircuitBreakerService circuitBreakerService) {
        this.circuitBreakerService = circuitBreakerService;
    }

    @GetMapping
    @Operation(summary = "List circuit breakers")
    public List<CircuitBreakerResponse> listCircuitBreakers(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader) {
        TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        return circuitBreakerService.listBreakers();
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get circuit breaker by name")
    public CircuitBreakerResponse getCircuitBreaker(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String name) {
        TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        return circuitBreakerService.getBreaker(name);
    }
}
