package com.playground.playground.api;

import com.playground.common.web.HealthSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Platform", description = "Service health endpoints")
public class HealthController {

    private static final String SERVICE_NAME = "playground-api";

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    @Operation(summary = "Application health")
    public ResponseEntity<Map<String, Object>> health() {
        return HealthSupport.healthResponse(SERVICE_NAME, jdbcTemplate);
    }
}
