-- Phase 3: Financial services schema

CREATE TABLE banking.audit_events (
    id             VARCHAR(36) PRIMARY KEY,
    tenant_id      VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    entity_type    VARCHAR(64) NOT NULL,
    entity_id      VARCHAR(36) NOT NULL,
    action         VARCHAR(64) NOT NULL,
    actor_id       VARCHAR(36),
    correlation_id VARCHAR(64),
    details        VARCHAR(1024),
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banking.idempotency_keys (
    tenant_id       VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    idempotency_key VARCHAR(128) NOT NULL,
    operation       VARCHAR(64) NOT NULL,
    request_hash    VARCHAR(64) NOT NULL,
    response_status INT NOT NULL,
    response_body   VARCHAR(4096) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMP NOT NULL,
    PRIMARY KEY (tenant_id, idempotency_key)
);

CREATE TABLE banking.financial_transactions (
    id              VARCHAR(36) PRIMARY KEY,
    tenant_id       VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    party_id        VARCHAR(36) NOT NULL REFERENCES banking.parties (id),
    account_id      VARCHAR(36) NOT NULL REFERENCES banking.accounts (id),
    txn_type        VARCHAR(32) NOT NULL,
    status          VARCHAR(32) NOT NULL,
    amount          NUMERIC(19, 4) NOT NULL,
    currency        VARCHAR(3) NOT NULL,
    reference       VARCHAR(128),
    description     VARCHAR(256),
    idempotency_key VARCHAR(128),
    version         BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banking.ledger_entries (
    id             VARCHAR(36) PRIMARY KEY,
    tenant_id      VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    transaction_id VARCHAR(36) NOT NULL REFERENCES banking.financial_transactions (id),
    account_id     VARCHAR(36) NOT NULL REFERENCES banking.accounts (id),
    entry_type     VARCHAR(16) NOT NULL,
    amount         NUMERIC(19, 4) NOT NULL,
    currency       VARCHAR(3) NOT NULL,
    balance_after  NUMERIC(19, 4) NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banking.payments (
    id             VARCHAR(36) PRIMARY KEY,
    tenant_id      VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    transaction_id VARCHAR(36) NOT NULL UNIQUE REFERENCES banking.financial_transactions (id),
    party_id       VARCHAR(36) NOT NULL REFERENCES banking.parties (id),
    account_id     VARCHAR(36) NOT NULL REFERENCES banking.accounts (id),
    beneficiary_id VARCHAR(36) NOT NULL REFERENCES banking.beneficiaries (id),
    amount         NUMERIC(19, 4) NOT NULL,
    currency       VARCHAR(3) NOT NULL,
    reference      VARCHAR(128),
    status         VARCHAR(32) NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banking.transfers (
    id               VARCHAR(36) PRIMARY KEY,
    tenant_id        VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    transaction_id   VARCHAR(36) NOT NULL UNIQUE REFERENCES banking.financial_transactions (id),
    from_account_id  VARCHAR(36) NOT NULL REFERENCES banking.accounts (id),
    to_account_id    VARCHAR(36) NOT NULL REFERENCES banking.accounts (id),
    amount           NUMERIC(19, 4) NOT NULL,
    currency         VARCHAR(3) NOT NULL,
    reference        VARCHAR(128),
    status           VARCHAR(32) NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banking.cards (
    id           VARCHAR(36) PRIMARY KEY,
    tenant_id    VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    party_id     VARCHAR(36) NOT NULL REFERENCES banking.parties (id),
    account_id   VARCHAR(36) NOT NULL REFERENCES banking.accounts (id),
    pan_last4    VARCHAR(4) NOT NULL,
    product_code VARCHAR(32) NOT NULL,
    status       VARCHAR(32) NOT NULL,
    version      BIGINT NOT NULL DEFAULT 0,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banking.card_authorizations (
    id             VARCHAR(36) PRIMARY KEY,
    tenant_id      VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    card_id        VARCHAR(36) NOT NULL REFERENCES banking.cards (id),
    transaction_id VARCHAR(36) NOT NULL UNIQUE REFERENCES banking.financial_transactions (id),
    merchant_name  VARCHAR(128) NOT NULL,
    amount         NUMERIC(19, 4) NOT NULL,
    currency       VARCHAR(3) NOT NULL,
    status         VARCHAR(32) NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banking.fx_quotes (
    id                VARCHAR(36) PRIMARY KEY,
    tenant_id         VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    from_currency     VARCHAR(3) NOT NULL,
    to_currency       VARCHAR(3) NOT NULL,
    rate              NUMERIC(19, 8) NOT NULL,
    from_amount       NUMERIC(19, 4) NOT NULL,
    to_amount         NUMERIC(19, 4) NOT NULL,
    expires_at        TIMESTAMP NOT NULL,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banking.fx_conversions (
    id               VARCHAR(36) PRIMARY KEY,
    tenant_id        VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    transaction_id   VARCHAR(36) NOT NULL UNIQUE REFERENCES banking.financial_transactions (id),
    quote_id         VARCHAR(36) NOT NULL REFERENCES banking.fx_quotes (id),
    from_account_id  VARCHAR(36) NOT NULL REFERENCES banking.accounts (id),
    to_account_id    VARCHAR(36) NOT NULL REFERENCES banking.accounts (id),
    from_amount      NUMERIC(19, 4) NOT NULL,
    to_amount        NUMERIC(19, 4) NOT NULL,
    from_currency    VARCHAR(3) NOT NULL,
    to_currency      VARCHAR(3) NOT NULL,
    rate             NUMERIC(19, 8) NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banking.statements (
    id               VARCHAR(36) PRIMARY KEY,
    tenant_id        VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    account_id       VARCHAR(36) NOT NULL REFERENCES banking.accounts (id),
    period_start     DATE NOT NULL,
    period_end       DATE NOT NULL,
    opening_balance  NUMERIC(19, 4) NOT NULL,
    closing_balance  NUMERIC(19, 4) NOT NULL,
    currency         VARCHAR(3) NOT NULL,
    status           VARCHAR(32) NOT NULL,
    version          BIGINT NOT NULL DEFAULT 0,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banking.statement_lines (
    id             VARCHAR(36) PRIMARY KEY,
    statement_id   VARCHAR(36) NOT NULL REFERENCES banking.statements (id),
    transaction_id VARCHAR(36) REFERENCES banking.financial_transactions (id),
    posted_at      TIMESTAMP NOT NULL,
    description    VARCHAR(256) NOT NULL,
    amount         NUMERIC(19, 4) NOT NULL,
    balance_after  NUMERIC(19, 4) NOT NULL,
    sort_order     INT NOT NULL
);

CREATE INDEX idx_audit_tenant_created ON banking.audit_events (tenant_id, created_at);
CREATE INDEX idx_fin_txn_account ON banking.financial_transactions (tenant_id, account_id, created_at);
CREATE INDEX idx_fin_txn_party ON banking.financial_transactions (tenant_id, party_id);
CREATE INDEX idx_ledger_account ON banking.ledger_entries (account_id, created_at);
CREATE INDEX idx_payments_party ON banking.payments (party_id);
CREATE INDEX idx_transfers_from ON banking.transfers (from_account_id);
CREATE INDEX idx_cards_party ON banking.cards (party_id);
CREATE INDEX idx_statements_account ON banking.statements (account_id, period_end);
