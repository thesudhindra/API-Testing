CREATE SCHEMA IF NOT EXISTS banking;

CREATE TABLE banking.tenants (
    id         VARCHAR(36)  PRIMARY KEY,
    name       VARCHAR(128) NOT NULL UNIQUE
);

CREATE TABLE banking.roles (
    id         VARCHAR(36) PRIMARY KEY,
    tenant_id  VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    name       VARCHAR(64) NOT NULL,
    CONSTRAINT uq_role_tenant_name UNIQUE (tenant_id, name)
);

CREATE TABLE banking.users (
    id            VARCHAR(36) PRIMARY KEY,
    tenant_id     VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    username      VARCHAR(128) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    party_id      VARCHAR(36),
    enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_tenant_username UNIQUE (tenant_id, username)
);

CREATE TABLE banking.user_roles (
    user_id VARCHAR(36) NOT NULL REFERENCES banking.users (id),
    role_id VARCHAR(36) NOT NULL REFERENCES banking.roles (id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE banking.parties (
    id         VARCHAR(36) PRIMARY KEY,
    tenant_id  VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    party_type VARCHAR(32) NOT NULL,
    status     VARCHAR(32) NOT NULL,
    first_name VARCHAR(128),
    last_name  VARCHAR(128),
    email      VARCHAR(256),
    version    BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banking.kyc_cases (
    id              VARCHAR(36) PRIMARY KEY,
    party_id        VARCHAR(36) NOT NULL REFERENCES banking.parties (id),
    status          VARCHAR(32) NOT NULL,
    level           VARCHAR(32) NOT NULL,
    decision_reason VARCHAR(512),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banking.accounts (
    id                VARCHAR(36) PRIMARY KEY,
    tenant_id         VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    party_id          VARCHAR(36) NOT NULL REFERENCES banking.parties (id),
    account_number    VARCHAR(34) NOT NULL UNIQUE,
    product_code      VARCHAR(32) NOT NULL,
    currency          VARCHAR(3) NOT NULL,
    status            VARCHAR(32) NOT NULL,
    available_balance NUMERIC(19, 4) NOT NULL DEFAULT 0,
    ledger_balance    NUMERIC(19, 4) NOT NULL DEFAULT 0,
    version           BIGINT NOT NULL DEFAULT 0,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banking.beneficiaries (
    id                          VARCHAR(36) PRIMARY KEY,
    tenant_id                   VARCHAR(36) NOT NULL REFERENCES banking.tenants (id),
    party_id                    VARCHAR(36) NOT NULL REFERENCES banking.parties (id),
    nickname                    VARCHAR(128) NOT NULL,
    status                      VARCHAR(32) NOT NULL,
    sort_code                   VARCHAR(16),
    account_number              VARCHAR(34),
    iban                        VARCHAR(34),
    verified_at                 TIMESTAMP,
    first_payment_allowed_after TIMESTAMP,
    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_parties_tenant ON banking.parties (tenant_id);
CREATE INDEX idx_accounts_party ON banking.accounts (party_id);
CREATE INDEX idx_beneficiaries_party ON banking.beneficiaries (party_id);
CREATE INDEX idx_kyc_party ON banking.kyc_cases (party_id);
