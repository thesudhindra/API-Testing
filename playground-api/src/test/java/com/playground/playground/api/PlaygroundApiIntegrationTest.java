package com.playground.playground.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlaygroundApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthIsPublicAndReturnsUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("playground-api"))
                .andExpect(jsonPath("$.components.database").value("UP"))
                .andExpect(header().exists("X-Correlation-Id"));
    }

    @Test
    void versionIsPublic() throws Exception {
        mockMvc.perform(get("/version"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").exists());
    }

    @Test
    void scenariosRequireAuthentication() throws Exception {
        mockMvc.perform(get("/v1/scenarios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized"));
    }

    @Test
    void scenariosCatalogListsSeedScenarios() throws Exception {
        mockMvc.perform(get("/v1/scenarios").with(httpBasic("learner", "learner")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.slug=='payment-happy-path')]").exists())
                .andExpect(jsonPath("$[?(@.slug=='idempotency-replay')]").exists());
    }

    @Test
    void publicMockEndpointReturnsConfiguredBody() throws Exception {
        mockMvc.perform(post("/v1/mocks/aml/screen/clear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("CLEAR"));
    }

    @Test
    void playgroundResetClearsLabTables() throws Exception {
        mockMvc.perform(post("/v1/playground/reset")
                        .with(httpBasic("learner", "learner"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"scope\":\"PLAYGROUND\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scope").value("PLAYGROUND"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void testDataRetailCustomerProfileCreatesHandles() throws Exception {
        mockMvc.perform(post("/v1/playground/test-data")
                        .with(httpBasic("learner", "learner"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"namespace":"lab-ns-1","profile":"retail-customer"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].entityType").value("PARTY"))
                .andExpect(jsonPath("$[0].entityId").value("party-customer-1"));
    }

    @Test
    void faultRuleCanBeCreated() throws Exception {
        mockMvc.perform(post("/v1/playground/faults")
                        .with(httpBasic("learner", "learner"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "targetService": "BANKING",
                                  "pathPattern": "/v1/payments",
                                  "faultType": "LATENCY",
                                  "config": {"delayMs": 500}
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.faultType").value("LATENCY"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void dashboardReturnsLabOverview() throws Exception {
        mockMvc.perform(get("/v1/playground/dashboard").with(httpBasic("learner", "learner")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceHealth.playground-api").value("UP"))
                .andExpect(jsonPath("$.scenarioCount").isNumber())
                .andExpect(jsonPath("$.configSummary").isArray());
    }
}
