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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FinancialServicesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void paymentAndIdempotentReplay() throws Exception {
        String token = loginToken();

        String body = """
                {
                  "partyId": "party-customer-1",
                  "accountId": "acct-customer-1",
                  "beneficiaryId": "ben-1",
                  "amount": 25.50,
                  "currency": "GBP",
                  "reference": "rent"
                }
                """;

        mockMvc.perform(post("/v1/payments")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Tenant-Id", "tenant-demo")
                        .header("Idempotency-Key", "pay-test-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(25.50))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(post("/v1/payments")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Tenant-Id", "tenant-demo")
                        .header("Idempotency-Key", "pay-test-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(25.50));
    }

    @Test
    void transferBetweenOwnAccounts() throws Exception {
        String token = loginToken();

        mockMvc.perform(post("/v1/transfers")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Tenant-Id", "tenant-demo")
                        .header("Idempotency-Key", "xfer-test-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fromAccountId": "acct-customer-1",
                                  "toAccountId": "acct-customer-2",
                                  "amount": 50.00,
                                  "currency": "GBP",
                                  "reference": "savings"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void listTransactionsWithFilters() throws Exception {
        String token = loginToken();

        mockMvc.perform(get("/v1/transactions")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Tenant-Id", "tenant-demo")
                        .param("accountId", "acct-customer-1")
                        .param("type", "PAYMENT")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    private String loginToken() throws Exception {
        MvcResult login = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"tenant-demo","username":"customer","password":"password"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(login.getResponse().getContentAsString());
        return body.get("accessToken").asText();
    }
}
