package com.playground.banking.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BankingApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void healthIsPublicAndReturnsUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("banking-api"))
                .andExpect(jsonPath("$.components.database").value("UP"))
                .andExpect(header().exists("X-Correlation-Id"));
    }

    @Test
    void loginAndMeFlowWorksForSeedCustomer() throws Exception {
        MvcResult login = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"tenant-demo","username":"customer","password":"password"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        JsonNode body = objectMapper.readTree(login.getResponse().getContentAsString());
        String token = body.get("accessToken").asText();

        mockMvc.perform(get("/v1/auth/me")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Tenant-Id", "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("customer"))
                .andExpect(jsonPath("$.partyId").value("party-customer-1"));
    }

    @Test
    void protectedEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/v1/roles"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.correlationId").exists());
    }

    @Test
    void customerCanReadOwnParty() throws Exception {
        mockMvc.perform(get("/v1/parties/party-customer-1")
                        .with(jwt().jwt(j -> j
                                .subject("user-customer")
                                .claim("tenant_id", "tenant-demo")
                                .claim("party_id", "party-customer-1")
                                .claim("roles", java.util.List.of("RETAIL_CUSTOMER"))))
                        .header("X-Tenant-Id", "tenant-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void customerCannotListAllParties() throws Exception {
        mockMvc.perform(get("/v1/parties")
                        .with(jwt().jwt(j -> j
                                .subject("user-customer")
                                .claim("tenant_id", "tenant-demo")
                                .claim("party_id", "party-customer-1")
                                .claim("roles", java.util.List.of("RETAIL_CUSTOMER"))))
                        .header("X-Tenant-Id", "tenant-demo"))
                .andExpect(status().isForbidden());
    }
}
