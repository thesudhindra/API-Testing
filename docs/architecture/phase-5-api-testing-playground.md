# Phase 5 — API Testing Playground

## Scope

Phase 5 adds the **learning platform** (`playground-api` on port **8083**). It does not introduce new banking or enterprise domain features; it orchestrates, documents, and exercises the existing APIs for QA/SDET training.

| Deliverable | Location |
|-------------|----------|
| Test Lab service | `playground-api/` |
| Scenario catalog | `playground-api/src/main/resources/scenarios/*.yaml` |
| Lab schema | `playground-api/src/main/resources/db/migration/` |
| Contract registry | `contracts/{service}/openapi.yaml` |
| Docker (full profile) | `docker-compose.yml` → `playground-api` |

## Architecture

```
┌─────────────────┐     health probes      ┌──────────────────┐
│  playground-api │ ─────────────────────► │ platform-api     │
│  (Test Lab)     │                        │ banking-api      │
│  :8083          │ ── JDBC reset/seed ──► │ enterprise-api   │
└────────┬────────┘                        └────────┬─────────┘
         │                                          │
         └──────────────────┬───────────────────────┘
                            ▼
                     PostgreSQL (schemas:
                     public, banking, enterprise, playground)
```

### Module boundaries

- **`playground-common`** — shared RFC 7807, correlation ID, pagination, JWT helpers (unchanged).
- **`platform-api` / `banking-api` / `enterprise-api`** — domain services (unchanged in Phase 5).
- **`playground-api`** — lab-only concerns: scenarios, mocks, faults, test-data handles, reset, dashboard, teaching metadata.

Cross-schema JDBC in reset/seed is intentional for a **local training environment**; production would use service APIs or event-driven reconciliation instead.

## Features

### Scenario Catalog (`GET /v1/scenarios`)

YAML scenarios on the classpath describe learning paths (steps, rubric, expected signals). Slugs include:

- `payment-happy-path`, `idempotency-replay`, `bola-party-access`
- `fraud-velocity-alert`, `concurrent-transfer-race`, `webhook-retry-dlq`

Runs are tracked in `playground.scenario_runs` via `POST /v1/scenarios/{slug}/runs`.

### Seed Data Generator (`POST /v1/playground/seed`)

Profiles: `retail-customer`, `high-balance`, `aml-ready`. Delegates to test-data service; `high-balance` and `aml-ready` insert into `banking` schema when PostgreSQL is shared.

### Mock Services

- Admin: `GET/POST /v1/playground/mocks` (Basic auth)
- Public mock server: `/v1/mocks/**` (no auth)
- Seed mocks: AML clear/review at `/v1/mocks/aml/screen/clear` and `/review`

### Fault Injection (`/v1/playground/faults`)

Rules stored in `playground.fault_rules` with TTL. Types: `LATENCY`, `ERROR_RATE`, `TIMEOUT`, `RESET`. Targets: `BANKING`, `ENTERPRISE`, `PLATFORM`, `MOCK`.

> Fault rules are persisted for lab configuration; downstream services apply faults when integrated with a gateway or sidecar. The lab documents and activates rules for learners.

### API Reset (`POST /v1/playground/reset`)

Scopes: `PLAYGROUND`, `BANKING`, `ENTERPRISE`, `ALL`. Clears transactional data and restores seed balances; audited in `playground.reset_audit`.

### Test Data APIs (`/v1/playground/test-data`)

Namespace-scoped handles referencing entities for cleanup tracking. Profiles mirror seed generator.

### Contract Registry (`GET /v1/contracts`)

Scans `contracts/{service}/*.yaml` from workspace or `/app/contracts` in Docker.

### Learner Dashboard (`GET /v1/playground/dashboard`)

Aggregates service health (local DB + remote `/health`), scenario count, active faults, recent runs, config summary.

### Playground Configuration (`GET /v1/playground/config`)

Key/value lab settings in `playground.playground_config` (tenant default, reset enabled, fault injection enabled).

### Performance Test Support (`GET /v1/playground/performance/profiles`)

Curated load-test profiles (throughput, duration, target paths) for k6/JMeter/Gatling exercises.

### Security Test Support (`GET /v1/playground/security/test-cases`)

BOLA, auth, tenant, and privilege-escalation cases with expected status codes.

### Concurrency Scenarios (`GET /v1/playground/concurrency/scenarios`)

Filters scenario catalog by `concurrency` tag; race profiles for parallel transfer labs.

### Failure Simulation (`GET /v1/playground/failures`)

Pre-built failure recipes mapping to fault rules and mock endpoints.

## Security

- HTTP Basic (`learner` / `learner` by default) for lab admin APIs.
- Public: `/health`, `/version`, `/v3/api-docs`, `/swagger-ui/**`, `/v1/mocks/**`.
- Banking/enterprise APIs continue to use JWT from `banking-api`.

## Docker

| Profile | Services |
|---------|----------|
| `full` | postgres + platform + banking + enterprise + **playground-api** |

```bash
make up-full
curl -s -u learner:learner http://localhost:8083/v1/playground/dashboard | jq
make reset-lab   # POST reset scope ALL
```

## Local development (without full Docker)

```bash
docker compose up postgres -d
mvn -pl playground-api spring-boot:run
```

Ensure platform, banking, and enterprise APIs are running for dashboard remote health `UP` and cross-service scenarios.

## Standards verification

| Area | Status |
|------|--------|
| REST | Versioned `/v1` paths, appropriate HTTP verbs and status codes |
| RFC 7807 | Via `playground-common` `GlobalExceptionHandler` |
| Swagger | springdoc at `/swagger-ui.html` |
| SOLID | Controllers → services → repositories; domain enums isolated |
| Tests | `PlaygroundApiIntegrationTest` (H2, playground schema) |
| Docker | `playground-api/Dockerfile`, `full` profile in Compose |

## Related docs

- [Phase 1 Foundation](phase-1-foundation.md)
- [Phase 2 Core Banking](phase-2-core-banking.md)
- [Phase 3 Financial Services](phase-3-financial-services.md)
- [Phase 4 Enterprise Features](phase-4-enterprise-features.md)
- [Phase 5 Implementation Report](../implementation-report-phase-5.md)
