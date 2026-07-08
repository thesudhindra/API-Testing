package com.playground.enterprise.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EnterpriseApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthIsPublicAndReturnsUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("enterprise-api"));
    }

    @Test
    void customerCanCreateLoan() throws Exception {
        mockMvc.perform(post("/v1/loans")
                        .with(jwt().jwt(j -> j
                                .subject("user-customer")
                                .claim("tenant_id", "tenant-demo")
                                .claim("party_id", "party-customer-1")
                                .claim("roles", java.util.List.of("RETAIL_CUSTOMER"))))
                        .header("X-Tenant-Id", "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "partyId": "party-customer-1",
                                  "accountId": "acct-customer-1",
                                  "productCode": "PERSONAL",
                                  "principal": 10000.00,
                                  "currency": "GBP",
                                  "interestRate": 5.5,
                                  "termMonths": 24
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.outstandingBalance").value(10000.00));
    }

    @Test
    void fraudScreenFlagsHighAmount() throws Exception {
        mockMvc.perform(post("/v1/fraud/screen")
                        .with(jwt().jwt(j -> j
                                .subject("user-customer")
                                .claim("tenant_id", "tenant-demo")
                                .claim("party_id", "party-customer-1")
                                .claim("roles", java.util.List.of("RETAIL_CUSTOMER"))))
                        .header("X-Tenant-Id", "tenant-demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "partyId": "party-customer-1",
                                  "entityType": "PAYMENT",
                                  "entityId": "pay-demo-1",
                                  "amount": 6000.00
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.riskScore").value(85))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void adminCanListTenantSettings() throws Exception {
        mockMvc.perform(get("/v1/admin/settings")
                        .with(jwt().jwt(j -> j
                                .subject("user-admin")
                                .claim("tenant_id", "tenant-demo")
                                .claim("roles", java.util.List.of("ADMIN"))))
                        .header("X-Tenant-Id", "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
