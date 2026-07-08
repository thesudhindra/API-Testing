package com.playground.platform.api;

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
class PlatformApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthIsPublicAndReturnsUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("platform-api"))
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
    void demoEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/v1/demo/errors/not-found"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void notFoundReturnsProblemJson() throws Exception {
        mockMvc.perform(get("/v1/demo/errors/not-found").with(httpBasic("test", "test")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.correlationId").exists());
    }

    @Test
    void validationReturns422ProblemJson() throws Exception {
        mockMvc.perform(post("/v1/demo/validate")
                        .with(httpBasic("test", "test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }
}
