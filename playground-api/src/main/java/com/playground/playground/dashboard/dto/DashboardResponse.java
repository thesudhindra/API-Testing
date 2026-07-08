package com.playground.playground.dashboard.dto;

import com.playground.playground.config.dto.ConfigEntryResponse;
import com.playground.playground.fault.dto.FaultRuleResponse;
import com.playground.playground.scenario.dto.ScenarioRunResponse;

import java.util.List;
import java.util.Map;

public record DashboardResponse(
        Map<String, String> serviceHealth,
        int scenarioCount,
        int activeFaultCount,
        List<ScenarioRunResponse> recentRuns,
        List<ConfigEntryResponse> configSummary
) {
}
