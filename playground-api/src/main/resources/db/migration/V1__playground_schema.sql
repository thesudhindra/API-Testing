-- Phase 5: API Testing Playground schema

CREATE SCHEMA IF NOT EXISTS playground;

CREATE TABLE playground.playground_config (
    config_key   VARCHAR(64) PRIMARY KEY,
    config_value VARCHAR(1024) NOT NULL,
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE playground.fault_rules (
    id              VARCHAR(36) PRIMARY KEY,
    target_service  VARCHAR(32) NOT NULL,
    path_pattern    VARCHAR(256) NOT NULL,
    fault_type      VARCHAR(32) NOT NULL,
    config_json     VARCHAR(1024) NOT NULL,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    expires_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE playground.mock_endpoints (
    id          VARCHAR(36) PRIMARY KEY,
    path        VARCHAR(256) NOT NULL,
    http_method VARCHAR(16) NOT NULL,
    status_code INT NOT NULL,
    response_body VARCHAR(4096) NOT NULL,
    delay_ms    INT NOT NULL DEFAULT 0,
    enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (path, http_method)
);

CREATE TABLE playground.test_data_handles (
    id          VARCHAR(36) PRIMARY KEY,
    namespace   VARCHAR(64) NOT NULL,
    entity_type VARCHAR(64) NOT NULL,
    entity_id   VARCHAR(36) NOT NULL,
    metadata    VARCHAR(1024),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE playground.scenario_runs (
    id            VARCHAR(36) PRIMARY KEY,
    scenario_slug VARCHAR(128) NOT NULL,
    status        VARCHAR(32) NOT NULL,
    started_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at  TIMESTAMP
);

CREATE TABLE playground.reset_audit (
    id         VARCHAR(36) PRIMARY KEY,
    scope      VARCHAR(32) NOT NULL,
    status     VARCHAR(32) NOT NULL,
    details    VARCHAR(1024),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
