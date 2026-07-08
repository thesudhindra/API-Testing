package com.playground.enterprise.api;

import com.playground.enterprise.admin.dto.TenantSettingResponse;
import com.playground.enterprise.admin.dto.UpdateSettingRequest;
import com.playground.enterprise.admin.service.AdminService;
import com.playground.common.security.TenantAccess;
import com.playground.common.api.ApiHeaders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/settings")
@Tag(name = "Admin", description = "Tenant administration")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    @Operation(summary = "List tenant settings (privileged)")
    public List<TenantSettingResponse> listSettings(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        return adminService.listSettings(tenantId);
    }

    @GetMapping("/{key}")
    @Operation(summary = "Get tenant setting by key (privileged)")
    public TenantSettingResponse getSetting(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String key) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        return adminService.getSetting(tenantId, key);
    }

    @PutMapping("/{key}")
    @Operation(summary = "Upsert tenant setting (privileged)")
    public TenantSettingResponse upsertSetting(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String key,
            @Valid @RequestBody UpdateSettingRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        return adminService.upsertSetting(tenantId, jwt.getSubject(), key, request);
    }
}
