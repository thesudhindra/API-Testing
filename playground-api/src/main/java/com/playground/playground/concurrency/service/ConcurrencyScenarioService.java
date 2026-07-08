package com.playground.playground.concurrency.service;

import com.playground.common.exception.ResourceNotFoundException;
import com.playground.playground.concurrency.dto.ConcurrencyScenario;
import com.playground.playground.concurrency.dto.RaceProfile;
import com.playground.playground.scenario.service.ScenarioCatalogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConcurrencyScenarioService {

    private final ScenarioCatalogService scenarioCatalogService;

    public ConcurrencyScenarioService(ScenarioCatalogService scenarioCatalogService) {
        this.scenarioCatalogService = scenarioCatalogService;
    }

    public List<ConcurrencyScenario> getScenarios() {
        return scenarioCatalogService.listScenarios().stream()
                .filter(s -> s.tags() != null && s.tags().contains("concurrency"))
                .map(s -> new ConcurrencyScenario(s.slug(), s.title(), s.difficulty(), s.description()))
                .toList();
    }

    public RaceProfile getRaceProfile(String slug) {
        if (!"concurrent-transfer-race".equals(slug)) {
            throw new ResourceNotFoundException("Concurrency race profile not found: " + slug);
        }
        return new RaceProfile(
                slug,
                "Concurrent Transfer Race",
                "banking-api",
                "POST",
                "/v1/transfers",
                5,
                Map.of(
                        "fromAccountId", "acct-customer-1",
                        "toAccountId", "acct-customer-2",
                        "amount", 100,
                        "currency", "GBP"
                ),
                "Fire parallel transfers from the same account; expect at least one 409 Conflict "
                        + "from optimistic locking while final balances remain consistent."
        );
    }
}
