# Test Lab API Documentation

**Base URL:** `http://localhost:8083`  
**Authentication:** HTTP Basic (`learner` / `learner`) on admin routes; public mock server has no auth  
**OpenAPI:** `http://localhost:8083/swagger-ui.html`

The Test Lab orchestrates API testing exercises: scenarios, mocks, fault rules, test data, environment reset, and teaching metadata for performance, security, and concurrency labs.

---

## Status

### Get API health

`GET /health`

Returns lab service and database health. **No authentication required.**

| Status code | Description |
|-------------|-------------|
| 200 OK | Service healthy |

**Example response:**

```json
{
  "status": "UP",
  "service": "playground-api",
  "components": { "database": "UP" }
}
```

---

### Get API version

`GET /version`

**No authentication required.**

| Status code | Description |
|-------------|-------------|
| 200 OK | Version metadata returned |

---

## Scenarios

YAML-defined learning paths. Six scenarios are pre-loaded.

| Slug | Title | Difficulty |
|------|-------|------------|
| `payment-happy-path` | Payment Happy Path | L1 |
| `idempotency-replay` | Idempotency Replay | L2 |
| `fraud-velocity-alert` | Fraud Velocity Alert | L2 |
| `bola-party-access` | BOLA Party Access | L2 |
| `concurrent-transfer-race` | Concurrent Transfer Race | L3 |
| `webhook-retry-dlq` | Webhook Retry and DLQ | L3 |

### List all scenarios

`GET /v1/scenarios`

Returns all scenario definitions including steps, rubric, and learning objectives.

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| Authorization | string | header | Yes | Basic credentials |

| Status code | Description |
|-------------|-------------|
| 200 OK | Scenario list returned |
| 401 Unauthorized | Missing auth |

**What to test:** Response is JSON array with 6 items; each has `slug`, `title`, `steps`, `rubric`.

---

### Get scenario by slug

`GET /v1/scenarios/{slug}`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| slug | string | path | Yes | e.g. `payment-happy-path` |
| Authorization | string | header | Yes | Basic credentials |

| Status code | Description |
|-------------|-------------|
| 200 OK | Scenario found |
| 404 Not Found | Unknown slug |
| 401 Unauthorized | Missing auth |

---

### Start scenario run

`POST /v1/scenarios/{slug}/runs`

Creates a tracked run record with status `RUNNING`.

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| slug | string | path | Yes | Scenario slug |
| Authorization | string | header | Yes | Basic credentials |

| Status code | Description |
|-------------|-------------|
| 201 Created | Run started |
| 404 Not Found | Unknown slug |

**Example response:**

```json
{
  "id": "run-uuid-here",
  "scenarioSlug": "payment-happy-path",
  "status": "RUNNING",
  "startedAt": "2026-07-08T10:00:00Z",
  "completedAt": null
}
```

**What to test:** Save `id` for completion step; appears in dashboard `recentRuns`.

---

### Complete scenario run

`PATCH /v1/scenarios/runs/{runId}`

The request body must be in JSON format.

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| runId | string | path | Yes | Run ID from start step |
| status | string | body | Yes | `COMPLETED` or `FAILED` |
| Authorization | string | header | Yes | Basic credentials |

**Example request body:**

```json
{
  "status": "COMPLETED"
}
```

| Status code | Description |
|-------------|-------------|
| 200 OK | Run updated |
| 404 Not Found | Run not found |

---

## Contracts

### List contract files

`GET /v1/contracts`

Scans `contracts/{service}/*.yaml` from workspace or Docker `/app/contracts`.

| Status code | Description |
|-------------|-------------|
| 200 OK | Contract summaries returned |

**Example response:**

```json
[
  { "service": "banking-api", "filename": "openapi.yaml", "path": "..." }
]
```

---

### Get contract content

`GET /v1/contracts/{service}/{file}`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| service | string | path | Yes | e.g. `banking-api` |
| file | string | path | Yes | e.g. `openapi.yaml` |

| Status code | Description |
|-------------|-------------|
| 200 OK | YAML content returned |
| 404 Not Found | File not found |

---

## Configuration

### List config entries

`GET /v1/playground/config`

| Status code | Description |
|-------------|-------------|
| 200 OK | All config key/value pairs |

**Seeded values:**

| Key | Value |
|-----|-------|
| `default_tenant` | `tenant-demo` |
| `reset_enabled` | `true` |
| `fault_injection_enabled` | `true` |
| `max_test_data_handles` | `1000` |

---

### Get config by key

`GET /v1/playground/config/{key}`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| key | string | path | Yes | Config key |

---

### Update config

`PUT /v1/playground/config/{key}`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| key | string | path | Yes | Config key |
| value | string | body | Yes | New value (max 1024 chars) |

**Example request body:**

```json
{
  "value": "tenant-demo"
}
```

---

## Fault Injection

### List fault rules

`GET /v1/playground/faults`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| enabledOnly | boolean | query | No | If `true`, return only enabled rules |

---

### Create fault rule

`POST /v1/playground/faults`

Rules expire after 60 minutes by default.

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| targetService | string | body | Yes | `BANKING`, `ENTERPRISE`, `PLATFORM`, `MOCK` |
| pathPattern | string | body | Yes | Path pattern e.g. `/v1/payments` |
| faultType | string | body | Yes | `LATENCY`, `ERROR_RATE`, `TIMEOUT`, `RESET` |
| config | object | body | No | Fault-specific settings |

**Example request body:**

```json
{
  "targetService": "BANKING",
  "pathPattern": "/v1/payments",
  "faultType": "LATENCY",
  "config": { "delayMs": 500 }
}
```

| Status code | Description |
|-------------|-------------|
| 201 Created | Rule created |
| 400 Bad Request | Invalid body |

**What to test:** Create rule → list with `enabledOnly=true` → disable via DELETE.

---

### Disable fault rule

`DELETE /v1/playground/faults/{id}`

| Status code | Description |
|-------------|-------------|
| 204 No Content | Rule disabled |
| 404 Not Found | Rule not found |

---

## Mocks

### List mock endpoints

`GET /v1/playground/mocks`

---

### Create mock endpoint

`POST /v1/playground/mocks`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| path | string | body | Yes | Full path e.g. `/v1/mocks/webhooks/fail` |
| httpMethod | string | body | Yes | `GET`, `POST`, etc. |
| statusCode | integer | body | Yes | HTTP status 100–599 |
| responseBody | string | body | Yes | JSON string returned to caller |
| delayMs | integer | body | No | Artificial delay (default 0) |
| enabled | boolean | body | No | Default `true` |

**Example request body (webhook failure mock):**

```json
{
  "path": "/v1/mocks/webhooks/fail",
  "httpMethod": "POST",
  "statusCode": 500,
  "responseBody": "{\"error\":\"webhook delivery failed\"}",
  "delayMs": 0,
  "enabled": true
}
```

| Status code | Description |
|-------------|-------------|
| 201 Created | Mock created |
| 409 Conflict | Path + method already exists |

**Pre-seeded mocks (no auth to call):**

| Method | Path | Status | Response |
|--------|------|--------|----------|
| POST | `/v1/mocks/aml/screen/clear` | 200 | `{"result":"CLEAR","matchScore":0}` |
| POST | `/v1/mocks/aml/screen/review` | 200 | `{"result":"REVIEW","matchScore":75}` (200ms delay) |

---

### Serve mock response (public)

`* /v1/mocks/**`

**No authentication required.** Matches configured path and HTTP method exactly.

| Status code | Description |
|-------------|-------------|
| 200 OK | (or configured status) |
| 404 Not Found | No mock configured |

**Example:**

```
POST http://localhost:8083/v1/mocks/aml/screen/clear
Content-Type: application/json

{}
```

---

## Test Data

### Generate test data

`POST /v1/playground/test-data`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| namespace | string | body | Yes | Logical grouping for cleanup (max 64 chars) |
| profile | string | body | Yes | `retail-customer`, `high-balance`, or `aml-ready` |

**Example request body:**

```json
{
  "namespace": "lab-session-1",
  "profile": "retail-customer"
}
```

| Profile | Behaviour |
|---------|-----------|
| `retail-customer` | References seed `party-customer-1` and `acct-customer-1` |
| `high-balance` | Creates new party/account with £500,000 |
| `aml-ready` | Creates party flagged for AML screening labs |

| Status code | Description |
|-------------|-------------|
| 201 Created | Handles returned |
| 400 Bad Request | Unknown profile |

---

### List test data handles

`GET /v1/playground/test-data`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| namespace | string | query | Yes | Namespace to list |

---

### Delete test data namespace

`DELETE /v1/playground/test-data/{namespace}`

| Status code | Description |
|-------------|-------------|
| 204 No Content | Handles deleted |

---

## Seed Generator

### Generate seed by profile

`POST /v1/playground/seed`

Same profiles as test data; auto-generates namespace `seed-<random>`.

**Example request body:**

```json
{
  "profile": "retail-customer"
}
```

**Example response:**

```json
{
  "profile": "retail-customer",
  "entities": {
    "party": "party-customer-1",
    "account": "acct-customer-1"
  }
}
```

---

## Reset

### Reset lab data

`POST /v1/playground/reset`

Clears transactional data and restores seed balances. **Run at the start of each test session.**

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| scope | string | body | Yes | `PLAYGROUND`, `BANKING`, `ENTERPRISE`, or `ALL` |

**Example request body:**

```json
{
  "scope": "ALL"
}
```

| Scope | Effect |
|-------|--------|
| `PLAYGROUND` | Clears lab tables; re-seeds AML mocks; retains config |
| `BANKING` | Clears payments, transfers, etc.; restores account balances |
| `ENTERPRISE` | Clears loans, fraud, webhooks, jobs, etc. |
| `ALL` | All of the above |

| Status code | Description |
|-------------|-------------|
| 200 OK | Reset completed |

**Example response:**

```json
{
  "id": "reset-uuid",
  "scope": "ALL",
  "status": "COMPLETED",
  "details": "Playground lab tables cleared...",
  "createdAt": "2026-07-08T10:00:00Z"
}
```

---

## Dashboard

### Get learner dashboard

`GET /v1/playground/dashboard`

Aggregates service health, scenario count, active faults, recent runs, and config.

**Example response fields:**

```json
{
  "serviceHealth": {
    "playground-api": "UP",
    "platform-api": "UP",
    "banking-api": "UP",
    "enterprise-api": "UP"
  },
  "scenarioCount": 6,
  "activeFaultCount": 0,
  "recentRuns": [],
  "configSummary": []
}
```

**What to test:** After `make up-full`, all four services should show `UP`.

---

## Performance

### List performance profiles

`GET /v1/playground/performance/profiles`

Returns curated load-test hints for k6, JMeter, or Gatling.

| Profile | Path | Method | VUs | Duration (sec) |
|---------|------|--------|-----|----------------|
| `payment-throughput` | `/v1/payments` | POST | 20 | 120 |
| `transfer-burst` | `/v1/transfers` | POST | 50 | 60 |
| `account-read` | `/v1/accounts/{accountId}` | GET | 100 | 180 |
| `health-probe` | `/health` | GET | 200 | 30 |

**What to test:** Use profiles to configure Postman Collection Runner or external load tools against banking-api.

---

## Security Lab

### List security test cases

`GET /v1/playground/security/test-cases`

Returns curated negative security tests to execute against banking/enterprise APIs.

| id | Category | Service | Method | Path | Expected status |
|----|----------|---------|--------|------|-----------------|
| `bola-party-read` | BOLA | banking-api | GET | `/v1/parties/party-other` | 403 |
| `bola-account-read` | BOLA | banking-api | GET | `/v1/accounts/acct-other` | 403 |
| `missing-auth` | AUTH | banking-api | POST | `/v1/payments` | 401 |
| `wrong-tenant` | TENANT | banking-api | GET | `/v1/accounts?partyId=party-customer-1` | 403 |
| `privilege-escalation` | AUTHZ | enterprise-api | PUT | `/v1/admin/settings/fraud.velocity.threshold` | 403 |

**What to test:** For each case, execute the described request and assert expected status + problem+json.

---

## Concurrency

### List concurrency scenarios

`GET /v1/playground/concurrency/scenarios`

Returns scenarios tagged `concurrency` (currently `concurrent-transfer-race`).

---

### Get race profile

`GET /v1/playground/concurrency/scenarios/{slug}/race-profile`

Only `concurrent-transfer-race` is supported today.

**Returns:** 5 parallel `POST /v1/transfers` with payload template using `acct-customer-1` → `acct-customer-2`, amount `100` GBP.

**What to test:** Postman Collection Runner with 5 iterations; expect at least one `409 Conflict`.

---

## Failure Simulation

### List failure simulations

`GET /v1/playground/failures`

Pre-built recipes for fault injection labs.

| slug | Description |
|------|-------------|
| `banking-latency-spike` | 2s latency on `/v1/payments/**` |
| `enterprise-error-burst` | 30% 503 on `/v1/aml/screen` |
| `mock-aml-review` | Route to AML review mock |
| `platform-timeout` | 5s timeout on `/health` |

---

### Get failure simulation

`GET /v1/playground/failures/{slug}`

Use the returned recipe to create a matching `POST /v1/playground/faults` rule.

---

[← Back to index](README.md) · [Platform API ←](platform-api.md)
