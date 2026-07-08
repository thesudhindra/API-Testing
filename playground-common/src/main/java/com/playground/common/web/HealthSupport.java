package com.playground.common.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

public final class HealthSupport {

    private HealthSupport() {
    }

    public static ResponseEntity<Map<String, Object>> healthResponse(String serviceName, JdbcTemplate jdbcTemplate) {
        Map<String, Object> components = new LinkedHashMap<>();
        String dbStatus = checkDatabase(jdbcTemplate);
        components.put("database", dbStatus);

        String overall = "UP".equals(dbStatus) ? "UP" : "DOWN";
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", overall);
        body.put("service", serviceName);
        body.put("components", components);

        if ("DOWN".equals(overall)) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
        }
        return ResponseEntity.ok(body);
    }

    public static String checkDatabase(JdbcTemplate jdbcTemplate) {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "UP";
        } catch (Exception ex) {
            return "DOWN";
        }
    }
}
