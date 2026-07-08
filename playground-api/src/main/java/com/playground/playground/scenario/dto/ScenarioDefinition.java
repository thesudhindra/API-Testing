package com.playground.playground.scenario.dto;

import java.util.List;
import java.util.Map;

public record ScenarioDefinition(
        String slug,
        String title,
        String difficulty,
        String description,
        List<String> learningObjectives,
        List<String> tags,
        List<Map<String, Object>> steps,
        List<Map<String, Object>> rubric,
        List<String> expectedSignals
) {
}
