-- Phase 4: Enterprise features schema

CREATE SCHEMA IF NOT EXISTS enterprise;

CREATE TABLE enterprise.loans (
    id                  VARCHAR(36) PRIMARY KEY,
    tenant_id           VARCHAR(36) NOT NULL,
    party_id            VARCHAR(36) NOT NULL,
    account_id          VARCHAR(36) NOT NULL,
    product_code        VARCHAR(32) NOT NULL,
    principal           NUMERIC(19, 4) NOT NULL,
    currency            VARCHAR(3) NOT NULL,
    interest_rate       NUMERIC(8, 4) NOT NULL,
    term_months         INT NOT NULL,
    status              VARCHAR(32) NOT NULL,
    outstanding_balance NUMERIC(19, 4) NOT NULL,
    version             BIGINT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.loan_repayments (
    id         VARCHAR(36) PRIMARY KEY,
    tenant_id  VARCHAR(36) NOT NULL,
    loan_id    VARCHAR(36) NOT NULL REFERENCES enterprise.loans (id),
    amount     NUMERIC(19, 4) NOT NULL,
    currency   VARCHAR(3) NOT NULL,
    status     VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.fixed_deposits (
    id            VARCHAR(36) PRIMARY KEY,
    tenant_id     VARCHAR(36) NOT NULL,
    party_id      VARCHAR(36) NOT NULL,
    account_id    VARCHAR(36) NOT NULL,
    principal     NUMERIC(19, 4) NOT NULL,
    currency      VARCHAR(3) NOT NULL,
    interest_rate NUMERIC(8, 4) NOT NULL,
    term_days     INT NOT NULL,
    maturity_date DATE NOT NULL,
    status        VARCHAR(32) NOT NULL,
    version       BIGINT NOT NULL DEFAULT 0,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.recurring_deposits (
    id                 VARCHAR(36) PRIMARY KEY,
    tenant_id          VARCHAR(36) NOT NULL,
    party_id           VARCHAR(36) NOT NULL,
    account_id         VARCHAR(36) NOT NULL,
    installment_amount NUMERIC(19, 4) NOT NULL,
    currency           VARCHAR(3) NOT NULL,
    frequency          VARCHAR(16) NOT NULL,
    status             VARCHAR(32) NOT NULL,
    next_due_date      DATE,
    version            BIGINT NOT NULL DEFAULT 0,
    created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.recurring_deposit_installments (
    id                   VARCHAR(36) PRIMARY KEY,
    tenant_id            VARCHAR(36) NOT NULL,
    recurring_deposit_id VARCHAR(36) NOT NULL REFERENCES enterprise.recurring_deposits (id),
    amount               NUMERIC(19, 4) NOT NULL,
    status               VARCHAR(32) NOT NULL,
    due_date             DATE NOT NULL,
    paid_at              TIMESTAMP,
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.notifications (
    id         VARCHAR(36) PRIMARY KEY,
    tenant_id  VARCHAR(36) NOT NULL,
    party_id   VARCHAR(36) NOT NULL,
    channel    VARCHAR(16) NOT NULL,
    subject    VARCHAR(256) NOT NULL,
    body       VARCHAR(2048) NOT NULL,
    status     VARCHAR(32) NOT NULL,
    read_at    TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.documents (
    id            VARCHAR(36) PRIMARY KEY,
    tenant_id     VARCHAR(36) NOT NULL,
    party_id      VARCHAR(36) NOT NULL,
    document_type VARCHAR(64) NOT NULL,
    file_name     VARCHAR(256) NOT NULL,
    content_type  VARCHAR(128) NOT NULL,
    storage_key   VARCHAR(256) NOT NULL,
    status        VARCHAR(32) NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.reports (
    id              VARCHAR(36) PRIMARY KEY,
    tenant_id       VARCHAR(36) NOT NULL,
    report_type     VARCHAR(64) NOT NULL,
    parameters      VARCHAR(1024),
    status          VARCHAR(32) NOT NULL,
    result_location VARCHAR(256),
    requested_by    VARCHAR(36),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at    TIMESTAMP
);

CREATE TABLE enterprise.fraud_alerts (
    id          VARCHAR(36) PRIMARY KEY,
    tenant_id   VARCHAR(36) NOT NULL,
    party_id    VARCHAR(36),
    entity_type VARCHAR(64) NOT NULL,
    entity_id   VARCHAR(36) NOT NULL,
    rule_code   VARCHAR(64) NOT NULL,
    risk_score  INT NOT NULL,
    status      VARCHAR(32) NOT NULL,
    details     VARCHAR(1024),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.aml_cases (
    id          VARCHAR(36) PRIMARY KEY,
    tenant_id   VARCHAR(36) NOT NULL,
    party_id    VARCHAR(36) NOT NULL,
    case_type   VARCHAR(32) NOT NULL,
    status      VARCHAR(32) NOT NULL,
    priority    VARCHAR(16) NOT NULL,
    assigned_to VARCHAR(36),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.aml_screenings (
    id              VARCHAR(36) PRIMARY KEY,
    tenant_id       VARCHAR(36) NOT NULL,
    party_id        VARCHAR(36) NOT NULL,
    screening_type  VARCHAR(32) NOT NULL,
    result          VARCHAR(32) NOT NULL,
    match_score     INT,
    case_id         VARCHAR(36) REFERENCES enterprise.aml_cases (id),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.audit_events (
    id             VARCHAR(36) PRIMARY KEY,
    tenant_id      VARCHAR(36) NOT NULL,
    entity_type    VARCHAR(64) NOT NULL,
    entity_id      VARCHAR(36) NOT NULL,
    action         VARCHAR(64) NOT NULL,
    actor_id       VARCHAR(36),
    correlation_id VARCHAR(64),
    details        VARCHAR(1024),
    prev_hash      VARCHAR(64),
    event_hash     VARCHAR(64) NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.tenant_settings (
    tenant_id     VARCHAR(36) NOT NULL,
    setting_key   VARCHAR(64) NOT NULL,
    setting_value VARCHAR(1024) NOT NULL,
    updated_by    VARCHAR(36),
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tenant_id, setting_key)
);

CREATE TABLE enterprise.scheduled_tasks (
    id              VARCHAR(36) PRIMARY KEY,
    tenant_id       VARCHAR(36),
    task_type       VARCHAR(64) NOT NULL,
    cron_expression VARCHAR(64) NOT NULL,
    payload         VARCHAR(1024),
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    last_run_at     TIMESTAMP,
    next_run_at     TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.background_jobs (
    id            VARCHAR(36) PRIMARY KEY,
    tenant_id     VARCHAR(36) NOT NULL,
    job_type      VARCHAR(64) NOT NULL,
    payload       VARCHAR(2048),
    status        VARCHAR(32) NOT NULL,
    progress      INT NOT NULL DEFAULT 0,
    result        VARCHAR(2048),
    error_message VARCHAR(1024),
    retry_count   INT NOT NULL DEFAULT 0,
    max_retries   INT NOT NULL DEFAULT 3,
    version       BIGINT NOT NULL DEFAULT 0,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.webhooks (
    id          VARCHAR(36) PRIMARY KEY,
    tenant_id   VARCHAR(36) NOT NULL,
    event_type  VARCHAR(64) NOT NULL,
    target_url  VARCHAR(512) NOT NULL,
    secret      VARCHAR(128) NOT NULL,
    status      VARCHAR(32) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.webhook_deliveries (
    id            VARCHAR(36) PRIMARY KEY,
    tenant_id     VARCHAR(36) NOT NULL,
    webhook_id    VARCHAR(36) NOT NULL REFERENCES enterprise.webhooks (id),
    event_id      VARCHAR(36) NOT NULL,
    status        VARCHAR(32) NOT NULL,
    http_status   INT,
    attempt_count INT NOT NULL DEFAULT 0,
    last_error    VARCHAR(1024),
    next_retry_at TIMESTAMP,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at  TIMESTAMP
);

CREATE TABLE enterprise.domain_events (
    id             VARCHAR(36) PRIMARY KEY,
    tenant_id      VARCHAR(36) NOT NULL,
    aggregate_type VARCHAR(64) NOT NULL,
    aggregate_id   VARCHAR(36) NOT NULL,
    event_type     VARCHAR(64) NOT NULL,
    payload        VARCHAR(4096) NOT NULL,
    status         VARCHAR(32) NOT NULL,
    published_at   TIMESTAMP,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enterprise.circuit_breakers (
    name          VARCHAR(64) PRIMARY KEY,
    state         VARCHAR(16) NOT NULL,
    failure_count INT NOT NULL DEFAULT 0,
    success_count INT NOT NULL DEFAULT 0,
    opened_at     TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO enterprise.circuit_breakers (name, state, failure_count, success_count, updated_at) VALUES
    ('webhook-delivery', 'CLOSED', 0, 0, CURRENT_TIMESTAMP);
