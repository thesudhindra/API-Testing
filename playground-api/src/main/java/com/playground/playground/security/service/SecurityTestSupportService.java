package com.playground.playground.security.service;

import com.playground.playground.security.dto.SecurityTestCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityTestSupportService {

    public List<SecurityTestCase> getTestCases() {
        return List.of(
                new SecurityTestCase(
                        "bola-party-read",
                        "BOLA",
                        "Read another party's profile",
                        "Authenticate as customer and attempt GET on a different party_id; expect 403.",
                        "banking-api",
                        "GET",
                        "/v1/parties/party-other",
                        403),
                new SecurityTestCase(
                        "bola-account-read",
                        "BOLA",
                        "Read another customer's account",
                        "Use a valid JWT but request an account not owned by the token party_id.",
                        "banking-api",
                        "GET",
                        "/v1/accounts/acct-other",
                        403),
                new SecurityTestCase(
                        "missing-auth",
                        "AUTH",
                        "Unauthenticated payment creation",
                        "POST /v1/payments without Authorization header; expect 401 problem+json.",
                        "banking-api",
                        "POST",
                        "/v1/payments",
                        401),
                new SecurityTestCase(
                        "wrong-tenant",
                        "TENANT",
                        "Cross-tenant account listing",
                        "Supply X-Tenant-Id header that does not match JWT tenant claim.",
                        "banking-api",
                        "GET",
                        "/v1/accounts?partyId=party-customer-1",
                        403),
                new SecurityTestCase(
                        "privilege-escalation",
                        "AUTHZ",
                        "Customer invokes admin endpoint",
                        "Retail customer JWT calling enterprise admin settings; expect 403.",
                        "enterprise-api",
                        "PUT",
                        "/v1/admin/settings/fraud-threshold",
                        403)
        );
    }
}
