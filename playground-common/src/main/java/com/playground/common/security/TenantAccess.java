package com.playground.common.security;

import com.playground.common.exception.ForbiddenException;
import com.playground.common.exception.UnauthorizedException;
import org.springframework.security.oauth2.jwt.Jwt;

public final class TenantAccess {

    private TenantAccess() {
    }

    public static void requirePartyAccess(Jwt jwt, String partyId) {
        if (isPrivileged(jwt)) {
            return;
        }
        String tokenParty = jwt.getClaimAsString("party_id");
        if (tokenParty == null || !tokenParty.equals(partyId)) {
            throw new ForbiddenException("Access to this party is not permitted");
        }
    }

    public static void requirePrivileged(Jwt jwt) {
        if (!isPrivileged(jwt)) {
            throw new ForbiddenException("Admin or ops role required");
        }
    }

    public static boolean isPrivileged(Jwt jwt) {
        var roles = jwt.getClaimAsStringList("roles");
        if (roles == null) {
            return false;
        }
        return roles.contains("OPS_AGENT") || roles.contains("ADMIN");
    }

    public static String requireTenantHeader(String headerTenant, Jwt jwt) {
        String tokenTenant = jwt.getClaimAsString("tenant_id");
        if (tokenTenant == null) {
            throw new UnauthorizedException("Token missing tenant_id claim");
        }
        if (headerTenant != null && !headerTenant.isBlank() && !headerTenant.equals(tokenTenant)) {
            throw new ForbiddenException("X-Tenant-Id does not match token tenant");
        }
        return tokenTenant;
    }
}
