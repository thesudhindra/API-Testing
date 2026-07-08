package com.playground.playground.scenario.entity;

import com.playground.playground.domain.ScenarioRunStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(schema = "playground", name = "scenario_runs")
public class ScenarioRunEntity {

    @Id
    private String id;

    @Column(name = "scenario_slug", nullable = false, length = 128)
    private String scenarioSlug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ScenarioRunStatus status;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScenarioSlug() {
        return scenarioSlug;
    }

    public void setScenarioSlug(String scenarioSlug) {
        this.scenarioSlug = scenarioSlug;
    }

    public ScenarioRunStatus getStatus() {
        return status;
    }

    public void setStatus(ScenarioRunStatus status) {
        this.status = status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
