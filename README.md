# API Testing Playground

Enterprise API Testing Playground — a localhost training platform for experienced QA Engineers and SDETs. Banking domain semantics provide realism; every feature exists to teach API testing concepts.

**Phase 1 (Foundation)** delivers the platform skeleton. **Phase 2 (Core Banking)** adds identity, customer, KYC, accounts, and beneficiaries. **Phase 3 (Financial Services)** adds payments, transfers, ledger, cards, FX, statements, and transaction history. **Phase 4 (Enterprise Features)** adds loans, deposits, compliance, jobs, webhooks, and resilience in `enterprise-api`. **Phase 5 (API Testing Playground)** adds the Test Lab in `playground-api` for scenarios, mocks, faults, and learner tooling.

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker & Docker Compose (for local PostgreSQL and containerized APIs)

## Project structure

```
api-testing-playground/
├── playground-common/     # Shared library (errors, correlation ID, bootstrap helpers)
├── platform-api/          # Foundation Spring Boot service (:8080)
├── banking-api/           # Core banking + financial services (:8081)
├── enterprise-api/        # Enterprise features (:8082)
├── playground-api/        # API Testing Playground / Test Lab (:8083)
├── contracts/             # API contract artifacts (per-service)
├── docs/                  # Architecture and runbooks
├── docker-compose.yml
└── Makefile
```

## Quick start

### Compile and test (no Docker)

```bash
make build
make test
```

### Run with Docker (PostgreSQL + platform-api + banking-api)

```bash
cp .env.example .env   # optional — defaults work for local dev
make up-core
```

### Run all APIs (includes enterprise-api and test lab)

```bash
make up-full
```

### Verify endpoints

**Platform API (8080):**

```bash
curl -s http://localhost:8080/health | jq
curl -s -u learner:learner http://localhost:8080/v1/demo/errors/not-found | jq
```

**Banking API (8081):**

```bash
curl -s http://localhost:8081/health | jq
curl -s -X POST http://localhost:8081/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"tenantId":"tenant-demo","username":"customer","password":"password"}' | jq
```

OpenAPI UI:

- Platform: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Banking: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- Enterprise: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- Test Lab: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)

**Test Lab (8083):**

```bash
curl -s http://localhost:8083/health | jq
curl -s -u learner:learner http://localhost:8083/v1/scenarios | jq
curl -s -u learner:learner http://localhost:8083/v1/playground/dashboard | jq
make reset-lab   # reset PLAYGROUND + BANKING + ENTERPRISE data
```

## APIs

### Phase 1 — Platform (`platform-api`)

| Method | Path | Auth | Purpose |
|--------|------|------|---------|
| GET | `/health` | Public | Liveness + database check |
| GET | `/version` | Public | Build/version metadata |
| GET | `/v1/demo/errors/*` | Basic | RFC 7807 teaching endpoints |
| POST | `/v1/demo/validate` | Basic | Bean validation → 422 problem+json |

Default credentials: `learner` / `learner`

### Phase 2 — Core Banking (`banking-api`)

| Domain | Base path | Auth |
|--------|-----------|------|
| Authentication | `/v1/auth` | JWT (login public) |
| Users | `/v1/users` | JWT + RBAC |
| Roles | `/v1/roles` | JWT |
| Customers (parties) | `/v1/parties` | JWT + party BOLA |
| KYC | `/v1/kyc-cases` | JWT |
| Accounts | `/v1/accounts` | JWT |
| Beneficiaries | `/v1/beneficiaries` | JWT |

Seed users (tenant `tenant-demo`, password `password`): `customer`, `ops`, `admin`

### Phase 3 — Financial Services (`banking-api`)

| Domain | Base path | Auth |
|--------|-----------|------|
| Payments | `/v1/payments` | JWT + `Idempotency-Key` on POST |
| Transfers | `/v1/transfers` | JWT + `Idempotency-Key` on POST |
| Ledger | `/v1/ledger-entries` | JWT |
| Cards | `/v1/cards` | JWT |
| FX | `/v1/fx` | JWT + `Idempotency-Key` on conversions |
| Statements | `/v1/accounts/{id}/statements` | JWT |
| Transactions | `/v1/transactions` | JWT |

### Phase 4 — Enterprise (`enterprise-api`)

Authenticate via banking-api JWT. Service runs on port **8082**.

| Domain | Base path | Auth |
|--------|-----------|------|
| Loans | `/v1/loans` | JWT |
| Fixed Deposits | `/v1/fixed-deposits` | JWT |
| Recurring Deposits | `/v1/recurring-deposits` | JWT |
| Notifications | `/v1/notifications` | JWT |
| Documents | `/v1/documents` | JWT |
| Reports | `/v1/reports` | JWT |
| Fraud | `/v1/fraud` | JWT |
| AML | `/v1/aml` | JWT |
| Audit | `/v1/audit/events` | JWT (ops/admin) |
| Admin | `/v1/admin/settings` | JWT (admin) |
| Jobs | `/v1/jobs` | JWT |
| Scheduler | `/v1/scheduler/tasks` | JWT (ops/admin) |
| Webhooks | `/v1/webhooks` | JWT |
| Events | `/v1/events` | JWT (ops/admin) |
| Circuit Breakers | `/v1/resilience/circuit-breakers` | JWT |

### Phase 5 — Test Lab (`playground-api`)

Authenticate with HTTP Basic (`learner` / `learner`). Service runs on port **8083**.

| Domain | Base path | Auth |
|--------|-----------|------|
| Scenarios | `/v1/scenarios` | Basic |
| Contracts | `/v1/contracts` | Basic |
| Configuration | `/v1/playground/config` | Basic |
| Faults | `/v1/playground/faults` | Basic |
| Mocks (admin) | `/v1/playground/mocks` | Basic |
| Mocks (public) | `/v1/mocks/**` | Public |
| Test data | `/v1/playground/test-data` | Basic |
| Seed generator | `/v1/playground/seed` | Basic |
| Reset | `/v1/playground/reset` | Basic |
| Dashboard | `/v1/playground/dashboard` | Basic |
| Performance | `/v1/playground/performance` | Basic |
| Security lab | `/v1/playground/security` | Basic |
| Concurrency | `/v1/playground/concurrency` | Basic |
| Failure simulation | `/v1/playground/failures` | Basic |

## Cross-cutting standards

- **Errors:** `application/problem+json` (RFC 7807) with `type`, `title`, `status`, `detail`, `correlationId`
- **Correlation:** `X-Correlation-Id` request/response header; included in structured logs
- **Tenancy:** `X-Tenant-Id` header validated against JWT `tenant_id` claim (`TenantAccess` in `playground-common`)
- **Security:** Platform uses HTTP Basic; banking-api issues JWT; enterprise-api validates the same JWT

## Documentation

**Start here (one file, all endpoints):**

### → [API Testing Guide](docs/API-TESTING-GUIDE.md)

Postman environment: `docs/postman/playground-environment.json`

Other docs:
- [SDET Postman workflows](docs/sdet-postman-testing-guide.md) — scenario walkthroughs
- [Architecture phases](docs/architecture/) — how the system is built

## License

Internal training use.
