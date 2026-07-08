CREATE SCHEMA IF NOT EXISTS platform;

CREATE TABLE IF NOT EXISTS platform.platform_metadata (
    id         BIGSERIAL PRIMARY KEY,
    component  VARCHAR(64)  NOT NULL,
    version    VARCHAR(32)  NOT NULL,
    applied_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO platform.platform_metadata (component, version)
SELECT 'platform-api', '0.1.0'
WHERE NOT EXISTS (SELECT 1 FROM platform.platform_metadata WHERE component = 'platform-api');
