package com.playground.playground.scenario.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.common.exception.ResourceNotFoundException;
import com.playground.playground.domain.ScenarioRunStatus;
import com.playground.playground.scenario.dto.ScenarioDefinition;
import com.playground.playground.scenario.dto.ScenarioRunResponse;
import com.playground.playground.scenario.entity.ScenarioRunEntity;
import com.playground.playground.scenario.repository.ScenarioRunRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ScenarioCatalogService {

    private final ObjectMapper yamlObjectMapper;
    private final ScenarioRunRepository scenarioRunRepository;
    private final Map<String, ScenarioDefinition> scenarios = new ConcurrentHashMap<>();

    public ScenarioCatalogService(
            @Qualifier("yamlObjectMapper") ObjectMapper yamlObjectMapper,
            ScenarioRunRepository scenarioRunRepository) {
        this.yamlObjectMapper = yamlObjectMapper;
        this.scenarioRunRepository = scenarioRunRepository;
        loadScenarios();
    }

    private void loadScenarios() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:scenarios/*.yaml");
            for (Resource resource : resources) {
                @SuppressWarnings("unchecked")
                Map<String, Object> raw = yamlObjectMapper.readValue(resource.getInputStream(), Map.class);
                ScenarioDefinition definition = toDefinition(raw);
                scenarios.put(definition.slug(), definition);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load scenario definitions", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private ScenarioDefinition toDefinition(Map<String, Object> raw) {
        return new ScenarioDefinition(
                (String) raw.get("slug"),
                (String) raw.get("title"),
                (String) raw.get("difficulty"),
                (String) raw.get("description"),
                (List<String>) raw.getOrDefault("learningObjectives", List.of()),
                (List<String>) raw.getOrDefault("tags", List.of()),
                (List<Map<String, Object>>) raw.getOrDefault("steps", List.of()),
                (List<Map<String, Object>>) raw.getOrDefault("rubric", List.of()),
                (List<String>) raw.getOrDefault("expectedSignals", List.of())
        );
    }

    public List<ScenarioDefinition> listScenarios() {
        return scenarios.values().stream()
                .sorted(Comparator.comparing(ScenarioDefinition::slug))
                .toList();
    }

    public ScenarioDefinition getScenario(String slug) {
        ScenarioDefinition definition = scenarios.get(slug);
        if (definition == null) {
            throw new ResourceNotFoundException("Scenario not found: " + slug);
        }
        return definition;
    }

    @Transactional
    public ScenarioRunResponse startRun(String slug) {
        getScenario(slug);
        ScenarioRunEntity entity = new ScenarioRunEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setScenarioSlug(slug);
        entity.setStatus(ScenarioRunStatus.RUNNING);
        entity.setStartedAt(Instant.now());
        return toResponse(scenarioRunRepository.save(entity));
    }

    @Transactional
    public ScenarioRunResponse completeRun(String runId, ScenarioRunStatus status) {
        ScenarioRunEntity entity = scenarioRunRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Scenario run not found: " + runId));
        entity.setStatus(status);
        entity.setCompletedAt(Instant.now());
        return toResponse(scenarioRunRepository.save(entity));
    }

    private ScenarioRunResponse toResponse(ScenarioRunEntity entity) {
        return new ScenarioRunResponse(
                entity.getId(),
                entity.getScenarioSlug(),
                entity.getStatus(),
                entity.getStartedAt(),
                entity.getCompletedAt()
        );
    }
}
