package com.playground.playground.seed.service;

import com.playground.playground.seed.dto.SeedGenerationResponse;
import com.playground.playground.testdata.dto.TestDataHandleResponse;
import com.playground.playground.testdata.service.TestDataService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Generates lab seed data by profile.
 *
 * <p>Profiles:
 * <ul>
 *   <li>{@code retail-customer} — references seeded Jane Doe party and GBP current account</li>
 *   <li>{@code high-balance} — creates a new party/account with £500,000 balance</li>
 *   <li>{@code aml-ready} — creates a party flagged for AML screening labs</li>
 * </ul>
 */
@Service
public class SeedDataGeneratorService {

    private final TestDataService testDataService;

    public SeedDataGeneratorService(TestDataService testDataService) {
        this.testDataService = testDataService;
    }

    public SeedGenerationResponse generate(String profile) {
        String namespace = "seed-" + UUID.randomUUID().toString().substring(0, 8);
        List<TestDataHandleResponse> handles = testDataService.generate(namespace, profile);
        Map<String, String> entities = new LinkedHashMap<>();
        for (TestDataHandleResponse handle : handles) {
            entities.put(handle.entityType().name().toLowerCase(), handle.entityId());
        }
        return new SeedGenerationResponse(profile, entities);
    }
}
