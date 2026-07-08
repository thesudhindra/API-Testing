package com.playground.banking.api;

import com.playground.banking.identity.dto.RoleResponse;
import com.playground.banking.identity.service.RoleService;
import com.playground.common.security.TenantAccess;
import com.playground.common.api.ApiHeaders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/roles")
@Tag(name = "Roles", description = "Tenant role catalog")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @Operation(summary = "List roles in tenant")
    public List<RoleResponse> listRoles(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        return roleService.listRoles(tenantId);
    }
}
