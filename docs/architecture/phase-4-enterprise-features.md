# Phase 4 — Enterprise Features

Phase 4 adds the **enterprise-api** service (port 8082) with lending, deposits, compliance, platform orchestration, and resilience patterns.

## Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌───────────────────┐
│  platform-api   │     │   banking-api   │     │  enterprise-api   │
│     :8080       │     │     :8081       │     │      :8082        │
│  schema: public │     │ schema: banking │     │ schema: enterprise│
└────────┬────────┘     └────────┬────────┘     └─────────┬─────────┘
         │                       │                         │
         └───────────────────────┴─────────────────────────┘
                                 ▼
                          PostgreSQL (playground)
```

- **Auth:** JWT from `banking-api` `POST /v1/auth/login` (shared `PLAYGROUND_JWT_SECRET`)
- **Multi-tenancy:** `X-Tenant-Id` validated against JWT `tenant_id` claim
- **Shared library:** `playground-common` (`TenantAccess`, `PageResponse`, `JwtSupport`, `DigestSupport`)
- **Cross-service refs:** `party_id`, `account_id` reference banking entities by ID (no cross-schema FK)

## Scope

| Domain | Base path | Notes |
|--------|-----------|-------|
| Loans | `/v1/loans` | Origination, repayments |
| Fixed Deposits | `/v1/fixed-deposits` | Term deposits with maturity |
| Recurring Deposits | `/v1/recurring-deposits` | Installment schedules |
| Notifications | `/v1/notifications` | Multi-channel inbox |
| Documents | `/v1/documents` | Metadata registration |
| Reporting | `/v1/reports` | Async report jobs |
| Fraud | `/v1/fraud` | Velocity screening, alerts |
| AML | `/v1/aml` | Cases and screenings |
| Audit | `/v1/audit/events` | Hash-chained query API (ops/admin) |
| Admin | `/v1/admin/settings` | Tenant configuration |
| Background Jobs | `/v1/jobs` | Submit and poll async work |
| Scheduler | `/v1/scheduler/tasks` | Cron task registry |
| Webhooks | `/v1/webhooks` | HMAC-signed delivery with retry |
| Events | `/v1/events` | Outbox event query |
| Resilience | `/v1/resilience/circuit-breakers` | Circuit breaker state |

**Out of scope for Phase 4:** Playground fault-injection features (Test Lab).

## Cross-cutting patterns

| Pattern | Implementation |
|---------|----------------|
| Audit | `EnterpriseAuditService` — SHA-256 hash chain per tenant, `Propagation.MANDATORY` |
| Event processing | Transactional outbox (`domain_events`), `EventProcessor` polls and dispatches |
| Background jobs | `background_jobs` table, `JobProcessor` with optimistic locking and retries |
| Scheduler | `scheduled_tasks` with `SchedulerRunner` (configurable via `playground.enterprise.scheduler.enabled`) |
| Webhooks | HMAC-SHA256 `X-Webhook-Signature`, delivery retry with backoff |
| Retry | `RetryExecutor` for webhook delivery attempts |
| Circuit breaker | `CircuitBreakerService` — opens after 5 failures, half-open after 30s |

## Database

Flyway migrations in `enterprise-api/src/main/resources/db/migration/` under schema `enterprise`:

- `V1__enterprise_schema.sql` — all Phase 4 tables
- `V2__enterprise_seed.sql` — demo tenant settings and scheduled task

## API quick start

```bash
# Obtain JWT from banking-api
TOKEN=$(curl -s -X POST http://localhost:8081/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"tenantId":"tenant-demo","username":"customer","password":"password"}' | jq -r .accessToken)

# Create a loan
curl -s -X POST http://localhost:8082/v1/loans \
  -H "Authorization: Bearer $TOKEN" \
  -H 'X-Tenant-Id: tenant-demo' \
  -H 'Content-Type: application/json' \
  -d '{
    "partyId":"party-customer-1",
    "accountId":"acct-customer-1",
    "productCode":"PERSONAL",
    "principal":10000.00,
    "currency":"GBP",
    "interestRate":5.5,
    "termMonths":24
  }' | jq

# Fraud screen (amount > 5000 triggers alert)
curl -s -X POST http://localhost:8082/v1/fraud/screen \
  -H "Authorization: Bearer $TOKEN" \
  -H 'X-Tenant-Id: tenant-demo' \
  -H 'Content-Type: application/json' \
  -d '{"partyId":"party-customer-1","entityType":"PAYMENT","entityId":"x","amount":6000}' | jq
```

OpenAPI UI: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)

## Docker

```bash
make up-full   # postgres + platform-api + banking-api + enterprise-api
```

## Testing dimensions

Phase 4 endpoints support exercises in async job polling, webhook HMAC verification, circuit breaker state transitions, audit hash-chain integrity, AML case escalation, fraud velocity rules, and scheduler-driven side effects.
