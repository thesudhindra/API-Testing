package com.playground.playground.dashboard.service;

import com.playground.common.web.HealthSupport;
import com.playground.playground.config.PlaygroundProperties;
import com.playground.playground.config.service.PlaygroundConfigService;
import com.playground.playground.dashboard.dto.DashboardResponse;
import com.playground.playground.fault.service.FaultInjectionService;
import com.playground.playground.scenario.dto.ScenarioRunResponse;
import com.playground.playground.scenario.entity.ScenarioRunEntity;
import com.playground.playground.scenario.repository.ScenarioRunRepository;
import com.playground.playground.scenario.service.ScenarioCatalogService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private static final String PLAYGROUND_SERVICE = "playground-api";

    private final PlaygroundProperties properties;
    private final RestClient restClient;
    private final JdbcTemplate jdbcTemplate;
    private final ScenarioCatalogService scenarioCatalogService;
    private final FaultInjectionService faultInjectionService;
    private final ScenarioRunRepository scenarioRunRepository;
    private final PlaygroundConfigService playgroundConfigService;

    public DashboardService(
            PlaygroundProperties properties,
            RestClient restClient,
            JdbcTemplate jdbcTemplate,
            ScenarioCatalogService scenarioCatalogService,
            FaultInjectionService faultInjectionService,
            ScenarioRunRepository scenarioRunRepository,
            PlaygroundConfigService playgroundConfigService) {
        this.properties = properties;
        this.restClient = restClient;
        this.jdbcTemplate = jdbcTemplate;
        this.scenarioCatalogService = scenarioCatalogService;
        this.faultInjectionService = faultInjectionService;
        this.scenarioRunRepository = scenarioRunRepository;
        this.playgroundConfigService = playgroundConfigService;
    }

    public DashboardResponse getDashboard() {
        Map<String, String> health = new LinkedHashMap<>();
        health.put(PLAYGROUND_SERVICE, HealthSupport.checkDatabase(jdbcTemplate));
        health.put("platform-api", checkRemoteHealth(properties.getPlatformUrl()));
        health.put("banking-api", checkRemoteHealth(properties.getBankingUrl()));
        health.put("enterprise-api", checkRemoteHealth(properties.getEnterpriseUrl()));

        List<ScenarioRunResponse> recentRuns = scenarioRunRepository.findTop10ByOrderByStartedAtDesc().stream()
                .map(this::toRunResponse)
                .toList();

        return new DashboardResponse(
                health,
                scenarioCatalogService.listScenarios().size(),
                faultInjectionService.listRules(true).size(),
                recentRuns,
                playgroundConfigService.listConfig()
        );
    }

    private String checkRemoteHealth(String baseUrl) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = restClient.get()
                    .uri(baseUrl + "/health")
                    .retrieve()
                    .body(Map.class);
            if (body != null && "UP".equals(body.get("status"))) {
                return "UP";
            }
            return "DOWN";
        } catch (Exception ex) {
            return "DOWN";
        }
    }

    private ScenarioRunResponse toRunResponse(ScenarioRunEntity entity) {
        return new ScenarioRunResponse(
                entity.getId(),
                entity.getScenarioSlug(),
                entity.getStatus(),
                entity.getStartedAt(),
                entity.getCompletedAt()
        );
    }
}
