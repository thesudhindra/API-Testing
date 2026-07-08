package com.playground.playground.scenario.repository;

import com.playground.playground.scenario.entity.ScenarioRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScenarioRunRepository extends JpaRepository<ScenarioRunEntity, String> {

    List<ScenarioRunEntity> findTop10ByOrderByStartedAtDesc();

    List<ScenarioRunEntity> findByScenarioSlugOrderByStartedAtDesc(String scenarioSlug);
}
