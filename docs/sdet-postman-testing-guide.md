# SDET & Student Guide — API Testing with Postman

> **Endpoint reference (grocery-store format):** [API-TESTING-GUIDE.md](API-TESTING-GUIDE.md) — one file, all endpoints, Postman-ready.

End-to-end guide for testing the **API Testing Playground** using Postman.

---

## 1. Prerequisites

| Requirement | Notes |
|-------------|-------|
| Java 21+, Maven 3.9+ | For `make build` / `make test` |
| Docker Desktop | Running |
| Postman | Desktop or web |
| `jq` (optional) | For curl examples |

**Start the full stack:**

```bash
cp .env.example .env   # Postgres on host port 5433 if 5432 is taken
make up-full
```

**Verify services:**

| Service | URL | Swagger UI |
|---------|-----|------------|
| Platform | `http://localhost:8080` | `/swagger-ui.html` |
| Banking | `http://localhost:8081` | `/swagger-ui.html` |
| Enterprise | `http://localhost:8082` | `/swagger-ui.html` |
| Test Lab | `http://localhost:8083` | `/swagger-ui.html` |

---

## 2. Postman Setup

### 2.1 Import environment

1. Postman → **Environments** → **Import**
2. Select [`docs/postman/playground-environment.json`](postman/playground-environment.json)
3. Activate **API Testing Playground — Local**

### 2.2 Import OpenAPI (optional)

Import each live spec for auto-generated requests:

| Service | Import URL |
|---------|------------|
| Platform | `http://localhost:8080/v3/api-docs` |
| Banking | `http://localhost:8081/v3/api-docs` |
| Enterprise | `http://localhost:8082/v3/api-docs` |
| Test Lab | `http://localhost:8083/v3/api-docs` |

### 2.3 Recommended collection structure

```
API Testing Playground/
├── 00 — Health & Setup
├── 01 — Test Lab (playground-api)
├── 02 — Platform API (Basic auth)
├── 03 — Banking API (JWT)
├── 04 — Enterprise API (JWT)
├── 05 — Lab Scenarios (cross-service)
└── 06 — Security & Negative Tests
```

### 2.4 Collection-level scripts

**Pre-request Script** (add to collection; skip for public endpoints):

```javascript
pm.environment.set("correlation_id", pm.variables.replaceIn("{{$guid}}"));
pm.request.headers.upsert({ key: "X-Correlation-Id", value: pm.environment.get("correlation_id") });
if (!pm.environment.get("idempotency_key")) {
    pm.environment.set("idempotency_key", pm.variables.replaceIn("{{$guid}}"));
}
```

**Test Script** (common assertions):

```javascript
pm.test("Correlation ID echoed", function () {
    pm.response.to.have.header("X-Correlation-Id");
});
pm.test("Problem JSON on errors", function () {
    if (pm.response.code >= 400) {
        pm.expect(pm.response.headers.get("Content-Type")).to.include("application/problem+json");
        const body = pm.response.json();
        pm.expect(body).to.have.property("correlationId");
        pm.expect(body).to.have.property("title");
    }
});
```

---

## 3. Authentication Cheat Sheet

| Service | Port | Auth type | How to authenticate in Postman |
|---------|------|-----------|----------------------------------|
| **platform-api** | 8080 | HTTP Basic | Auth tab → Basic → `learner` / `learner` |
| **banking-api** | 8081 | JWT Bearer | Login first; then Bearer `{{jwt_token}}` |
| **enterprise-api** | 8082 | JWT Bearer | Same JWT from banking-api login |
| **playground-api** | 8083 | HTTP Basic | Auth tab → Basic → `learner` / `learner` |

**Public endpoints (no auth):** `/health`, `/version`, `/swagger-ui/**`, `/v3/api-docs/**`, and **`/v1/mocks/**`** on the lab service.

### 3.1 Banking login (save JWT)

| Field | Value |
|-------|-------|
| **Method** | `POST` |
| **URL** | `{{banking_base}}/v1/auth/login` |
| **Auth** | No Auth |
| **Headers** | `Content-Type: application/json` |

**Body:**

```json
{
  "tenantId": "{{tenant_id}}",
  "username": "{{customer_username}}",
  "password": "{{customer_password}}"
}
```

**Tests tab script:**

```javascript
if (pm.response.code === 200) {
    const json = pm.response.json();
    pm.environment.set("jwt_token", json.accessToken);
}
```

**Seed users** (password for all: `password`):

| Username | Role | `party_id` | Use for |
|----------|------|------------|---------|
| `customer` | `RETAIL_CUSTOMER` | `party-customer-1` | Happy paths, BOLA negative tests |
| `ops` | `OPS_AGENT` | — | Privileged reads, KYC review |
| `admin` | `ADMIN` | — | User creation, admin settings, webhooks |

---

## 4. Headers Reference

| Header | Required when | Example |
|--------|---------------|---------|
| `Authorization` | Protected endpoints | `Bearer eyJ...` or Basic (Postman handles encoding) |
| `Content-Type` | POST/PATCH with JSON body | `application/json` |
| `X-Tenant-Id` | Banking & enterprise (recommended) | `tenant-demo` — must match JWT `tenant_id` |
| `X-Correlation-Id` | Optional everywhere | `{{correlation_id}}` — echoed in response |
| `Idempotency-Key` | **Required** on banking financial POSTs | `{{idempotency_key}}` — unique per logical operation |

**Endpoints requiring `Idempotency-Key`:**

- `POST /v1/payments`
- `POST /v1/transfers`
- `POST /v1/cards/{cardId}/authorizations`
- `POST /v1/fx/conversions`

---

## 5. Seed Data Reference

Use these IDs in request bodies without creating data first.

### Tenant

| ID | Name |
|----|------|
| `tenant-demo` | Demo Bank |

### Party & KYC

| Entity | ID | Notes |
|--------|-----|-------|
| Party | `party-customer-1` | Jane Doe, `ACTIVE` |
| KYC case | `kyc-1` | `APPROVED`, level `STANDARD` |

### Accounts

| ID | Product | Currency | Balance |
|----|---------|----------|---------|
| `acct-customer-1` | CURRENT | GBP | 5,000.00 |
| `acct-customer-2` | SAVINGS | GBP | 1,000.00 |
| `acct-customer-eur` | CURRENT | EUR | 200.00 |

### Other banking seeds

| Entity | ID |
|--------|-----|
| Beneficiary (ACTIVE) | `ben-1` |
| Card (debit, last4 4242) | `card-1` |

### Enterprise seeds

| Setting key | Value |
|-------------|-------|
| `fraud.velocity.threshold` | `5000` |
| Circuit breaker | `webhook-delivery` (CLOSED) |

---

## 6. Error Format (assert on every negative test)

All services return **RFC 7807** `application/problem+json`:

```json
{
  "type": "https://playground.example/problems/forbidden",
  "title": "Forbidden",
  "status": 403,
  "detail": "Access to this party is not permitted",
  "correlationId": "abc-123-def"
}
```

**Postman assertions:**

- `pm.response.to.have.status(403)`
- `pm.expect(pm.response.json().title).to.eql("Forbidden")`
- `pm.expect(pm.response.json().correlationId).to.exist`

---

## 7. Service-by-Service API Reference

### 7.1 Test Lab — `playground-api` (:8083)

**Auth:** Basic `learner` / `learner` on all `/v1/**` except public mocks.

#### Health (no auth)

| Method | URL | Assert |
|--------|-----|--------|
| `GET` | `{{lab_base}}/health` | `status` = `UP`, `service` = `playground-api` |
| `GET` | `{{lab_base}}/version` | `version` exists |

#### Scenario catalog

| Method | URL | Body | Status |
|--------|-----|------|--------|
| `GET` | `{{lab_base}}/v1/scenarios` | — | `200` — array of 6 scenarios |
| `GET` | `{{lab_base}}/v1/scenarios/payment-happy-path` | — | `200` — steps + rubric |
| `POST` | `{{lab_base}}/v1/scenarios/payment-happy-path/runs` | — | `201` — save `id` as `scenario_run_id` |
| `PATCH` | `{{lab_base}}/v1/scenarios/runs/{{scenario_run_id}}` | `{"status":"COMPLETED"}` | `200` |

**Scenario slugs:** `payment-happy-path`, `idempotency-replay`, `fraud-velocity-alert`, `webhook-retry-dlq`, `concurrent-transfer-race`, `bola-party-access`

#### Reset environment

| Method | URL | Body |
|--------|-----|------|
| `POST` | `{{lab_base}}/v1/playground/reset` | `{"scope":"ALL"}` |

Scopes: `PLAYGROUND` | `BANKING` | `ENTERPRISE` | `ALL`

**When to reset:** Start of each test session; after destructive scenarios.

#### Test data & seed

| Method | URL | Body | Status |
|--------|-----|------|--------|
| `POST` | `{{lab_base}}/v1/playground/test-data` | see below | `201` |
| `GET` | `{{lab_base}}/v1/playground/test-data?namespace={{namespace}}` | — | `200` |
| `DELETE` | `{{lab_base}}/v1/playground/test-data/{{namespace}}` | — | `204` |
| `POST` | `{{lab_base}}/v1/playground/seed` | `{"profile":"retail-customer"}` | `201` |

**Test data body:**

```json
{
  "namespace": "{{namespace}}",
  "profile": "retail-customer"
}
```

Profiles: `retail-customer` | `high-balance` | `aml-ready`

#### Mocks

**Admin (Basic auth):**

```json
POST {{lab_base}}/v1/playground/mocks
{
  "path": "/v1/mocks/webhooks/fail",
  "httpMethod": "POST",
  "statusCode": 500,
  "responseBody": "{\"error\":\"webhook delivery failed\"}",
  "delayMs": 0,
  "enabled": true
}
```

**Public (no auth):**

```
POST {{lab_base}}/v1/mocks/aml/screen/clear
Body: {}
→ {"result":"CLEAR","matchScore":0}
```

#### Fault injection

```json
POST {{lab_base}}/v1/playground/faults
{
  "targetService": "BANKING",
  "pathPattern": "/v1/payments",
  "faultType": "LATENCY",
  "config": { "delayMs": 500 }
}
```

`targetService`: `BANKING` | `ENTERPRISE` | `PLATFORM` | `MOCK`  
`faultType`: `LATENCY` | `ERROR_RATE` | `TIMEOUT` | `RESET`

#### Teaching APIs (GET only)

| URL | Purpose |
|-----|---------|
| `{{lab_base}}/v1/playground/dashboard` | Service health, scenario count, recent runs |
| `{{lab_base}}/v1/playground/security/test-cases` | BOLA, auth, tenant test matrix |
| `{{lab_base}}/v1/playground/performance/profiles` | k6/JMeter load hints |
| `{{lab_base}}/v1/playground/concurrency/scenarios` | Concurrency-tagged scenarios |
| `{{lab_base}}/v1/playground/concurrency/scenarios/concurrent-transfer-race/race-profile` | Parallel transfer template |
| `{{lab_base}}/v1/playground/failures` | Failure simulation recipes |
| `{{lab_base}}/v1/contracts` | Contract registry listing |

---

### 7.2 Platform API — `platform-api` (:8080)

**Auth:** Basic `learner` / `learner` on `/v1/demo/**`

| Method | URL | Auth | Body | Expected |
|--------|-----|------|------|----------|
| `GET` | `/health` | Public | — | `200`, `status: UP` |
| `GET` | `/version` | Public | — | `200` |
| `GET` | `/v1/demo/errors/not-found` | Basic | — | `404`, problem+json |
| `GET` | `/v1/demo/errors/bad-request` | Basic | — | `400` |
| `GET` | `/v1/demo/errors/internal` | Basic | — | `500` |
| `POST` | `/v1/demo/validate` | Basic | `{"name":"Jane"}` | `200` |
| `POST` | `/v1/demo/validate` | Basic | `{"name":""}` | `422`, `errors[]` |

**What to test:** RFC 7807 shape, correlation ID, validation field errors, 401 without auth.

---

### 7.3 Banking API — `banking-api` (:8081)

**Auth:** JWT from login. Set `Authorization: Bearer {{jwt_token}}` and `X-Tenant-Id: {{tenant_id}}`.

#### Auth & profile

| Method | URL | Body | Assert |
|--------|-----|------|--------|
| `POST` | `/v1/auth/login` | login JSON | `accessToken` present |
| `GET` | `/v1/auth/me` | — | `username`, `partyId` match seed |

#### Core banking

| Method | URL | Role | Query / body |
|--------|-----|------|--------------|
| `GET` | `/v1/parties/{{party_id}}` | Customer (own) | — |
| `GET` | `/v1/parties` | Ops/Admin | — |
| `GET` | `/v1/accounts?partyId={{party_id}}` | Customer | — |
| `GET` | `/v1/accounts/{{account_gbp_current}}` | Customer | — |
| `GET` | `/v1/beneficiaries?partyId={{party_id}}` | Customer | — |
| `GET` | `/v1/kyc-cases?partyId={{party_id}}` | Customer | — |

#### Payments (idempotency lab)

```
POST {{banking_base}}/v1/payments
Headers:
  Authorization: Bearer {{jwt_token}}
  X-Tenant-Id: {{tenant_id}}
  Idempotency-Key: {{idempotency_key}}
  Content-Type: application/json
```

```json
{
  "partyId": "{{party_id}}",
  "accountId": "{{account_gbp_current}}",
  "beneficiaryId": "{{beneficiary_id}}",
  "amount": 25.50,
  "currency": "GBP",
  "reference": "rent"
}
```

**Assert:** `201`, payment `status` = `COMPLETED`, balance decreases.

**Idempotency replay test:** Send the **same** request again with the **same** `Idempotency-Key` → `201`, no double debit.

**Idempotency conflict test:** Same key, **different** body → `409`.

#### Transfers

```json
POST {{banking_base}}/v1/transfers
Headers: Authorization, X-Tenant-Id, Idempotency-Key

{
  "fromAccountId": "{{account_gbp_current}}",
  "toAccountId": "{{account_gbp_savings}}",
  "amount": 50.00,
  "currency": "GBP",
  "reference": "savings"
}
```

#### Cards

```json
POST {{banking_base}}/v1/cards/{{card_id}}/authorizations
Headers: Authorization, X-Tenant-Id, Idempotency-Key

{
  "merchantName": "Coffee Shop",
  "amount": 4.50,
  "currency": "GBP"
}
```

#### FX

**Step 1 — Quote:**

```json
POST {{banking_base}}/v1/fx/quotes
{
  "fromCurrency": "GBP",
  "toCurrency": "EUR",
  "fromAmount": 100.00
}
```

Save `quoteId` from response.

**Step 2 — Convert** (requires `Idempotency-Key`):

```json
POST {{banking_base}}/v1/fx/conversions
{
  "quoteId": "<from quote>",
  "fromAccountId": "{{account_gbp_current}}",
  "toAccountId": "{{account_eur_current}}",
  "partyId": "{{party_id}}"
}
```

**FX rates (seeded):** GBP→EUR `1.17`, EUR→GBP `0.85`

#### Statements & history

| Method | URL |
|--------|-----|
| `POST` | `/v1/accounts/{{account_gbp_current}}/statements` — body: `{"periodStart":"2026-01-01","periodEnd":"2026-01-31"}` |
| `GET` | `/v1/transactions?accountId={{account_gbp_current}}` |
| `GET` | `/v1/ledger-entries?accountId={{account_gbp_current}}` |

---

### 7.4 Enterprise API — `enterprise-api` (:8082)

**Auth:** Same JWT as banking-api. Enterprise does **not** issue tokens.

#### Loans

```json
POST {{enterprise_base}}/v1/loans
Authorization: Bearer {{jwt_token}}
X-Tenant-Id: {{tenant_id}}

{
  "partyId": "{{party_id}}",
  "accountId": "{{account_gbp_current}}",
  "productCode": "PERSONAL",
  "principal": 10000.00,
  "currency": "GBP",
  "interestRate": 5.5,
  "termMonths": 24
}
```

```json
POST {{enterprise_base}}/v1/loans/{loanId}/repayments
{ "amount": 500.00 }
```

#### Fraud screening (scenario: `fraud-velocity-alert`)

```json
POST {{enterprise_base}}/v1/fraud/screen
{
  "partyId": "{{party_id}}",
  "entityType": "PAYMENT",
  "entityId": "pay-lab-1",
  "amount": 6000.00
}
```

**Assert:** amount > `5000` threshold → alert `status: OPEN`, `riskScore: 85`

#### AML

```json
POST {{enterprise_base}}/v1/aml/screenings
{
  "partyId": "{{party_id}}",
  "screeningType": "SANCTIONS"
}
```

#### Webhooks (scenario: `webhook-retry-dlq`)

Requires **ops/admin** JWT.

```json
POST {{enterprise_base}}/v1/webhooks
{
  "eventType": "FRAUD_ALERT_CREATED",
  "targetUrl": "http://localhost:8083/v1/mocks/webhooks/fail",
  "secret": "my-hmac-secret"
}
```

Create the failing mock on the lab **first** (see §7.1 Mocks).

#### Admin (privileged — ops/admin only)

| Method | URL |
|--------|-----|
| `GET` | `/v1/admin/settings` |
| `PUT` | `/v1/admin/settings/fraud.velocity.threshold` — body: `{"value":"6000"}` |
| `GET` | `/v1/audit/events` |
| `GET` | `/v1/jobs` |
| `GET` | `/v1/resilience/circuit-breakers` |

---

## 8. Lab Scenarios — Step-by-Step in Postman

### Scenario 1: `payment-happy-path` (L1)

| Step | Request | Assert |
|------|---------|--------|
| 0 | `POST {{lab_base}}/v1/playground/reset` → `{"scope":"ALL"}` | `COMPLETED` |
| 1 | `GET {{lab_base}}/v1/scenarios/payment-happy-path` | Read rubric |
| 2 | `POST {{lab_base}}/v1/scenarios/payment-happy-path/runs` | Save `runId` |
| 3 | Login → save `jwt_token` | `200` |
| 4 | `GET {{banking_base}}/v1/auth/me` | `partyId` = `party-customer-1` |
| 5 | `POST {{banking_base}}/v1/payments` (see §7.3) | `201`, `COMPLETED` |
| 6 | `GET {{banking_base}}/v1/transactions?accountId=acct-customer-1` | Payment visible |
| 7 | `PATCH {{lab_base}}/v1/scenarios/runs/{runId}` → `COMPLETED` | `200` |

### Scenario 2: `idempotency-replay` (L2)

| Step | Action | Assert |
|------|--------|--------|
| 1 | Set fixed `idempotency_key` = `idem-lab-1` | — |
| 2 | POST payment (first time) | `201` |
| 3 | POST **identical** payment again | `201`, same payment id, single debit |
| 4 | Change `amount`, keep same key | `409` |

### Scenario 3: `bola-party-access` (L2)

| Step | Action | Assert |
|------|--------|--------|
| 1 | `GET {{lab_base}}/v1/playground/security/test-cases` | Find `bola-party-read` |
| 2 | Login as `customer` | JWT with `party-customer-1` |
| 3 | `GET {{banking_base}}/v1/parties/party-other` | `403`, problem+json |

### Scenario 4: `fraud-velocity-alert` (L2)

| Step | Action | Assert |
|------|--------|--------|
| 1 | Login as `customer` | JWT |
| 2 | `POST {{enterprise_base}}/v1/fraud/screen` amount `6000` | Alert created |
| 3 | `GET {{enterprise_base}}/v1/fraud/alerts?partyId={{party_id}}` | `OPEN` alert |

### Scenario 5: `concurrent-transfer-race` (L3)

| Step | Action | Assert |
|------|--------|--------|
| 1 | `GET {{lab_base}}/v1/playground/concurrency/scenarios/concurrent-transfer-race/race-profile` | 5 parallel transfers |
| 2 | Postman **Collection Runner** — 5 iterations, same transfer body | At least one `409` |
| 3 | `GET` account balance | Consistent final balance |

### Scenario 6: `webhook-retry-dlq` (L3)

| Step | Action | Assert |
|------|--------|--------|
| 1 | Create mock `POST /v1/mocks/webhooks/fail` → `500` | Mock works (no auth) |
| 2 | Login as `ops` or `admin` | Privileged JWT |
| 3 | Register webhook → lab mock URL | `201` |
| 4 | Trigger fraud/event | Delivery attempts > 1 |
| 5 | `GET /v1/webhooks/{id}/deliveries` | Final status `DLQ` |

---

## 9. Negative Test Matrix

| # | Test | Request | Expected |
|---|------|---------|----------|
| 1 | No auth on protected route | `GET /v1/accounts` (no header) | `401` |
| 2 | BOLA — other party | Customer JWT → `GET /v1/parties/party-other` | `403` |
| 3 | Wrong tenant header | JWT `tenant-demo` + `X-Tenant-Id: wrong` | `403` |
| 4 | Missing idempotency key | `POST /v1/payments` without key | `400` |
| 5 | Insufficient funds | Payment amount `999999` | `400` Insufficient funds |
| 6 | Validation failure | Login with empty `username` | `422` |
| 7 | Customer → admin API | Customer JWT → `PUT /v1/admin/settings/fraud.velocity.threshold` | `403` |
| 8 | Not found | `GET /v1/payments/nonexistent-id` | `404` |
| 9 | Lab without auth | `GET /v1/scenarios` (no Basic) | `401` |
| 10 | Public mock works | `POST /v1/mocks/aml/screen/clear` (no auth) | `200` |

---

## 10. Session Workflow (copy-paste checklist)

```
□ make up-full (or confirm all 4 services healthy)
□ Import Postman environment
□ GET {{lab_base}}/health → UP
□ GET {{lab_base}}/v1/playground/dashboard → all services UP
□ POST {{lab_base}}/v1/playground/reset {"scope":"ALL"}
□ POST login → save jwt_token
□ Run scenario or API tests
□ Assert correlationId on every response
□ POST reset ALL before next session
```

**CLI reset alternative:**

```bash
make reset-lab
```

---

## 11. Troubleshooting

| Symptom | Fix |
|---------|-----|
| Connection refused on `:8083` | Run `make up-full`; wait ~30s for startup |
| Dashboard shows banking `DOWN` | Banking container not ready — check `docker compose ps` |
| `401` on lab APIs | Add Basic auth `learner`/`learner` |
| `403` on own account | Check `X-Tenant-Id` matches JWT |
| Payment fails “beneficiary not active” | Use seed `ben-1`, not a newly created beneficiary |
| Postgres port conflict | Set `PLAYGROUND_DB_PORT=5433` in `.env` |
| Docker daemon error | Start Docker Desktop |

---

## 12. Further Reading

- [Phase 5 Architecture](architecture/phase-5-api-testing-playground.md)
- [README](../README.md)
- Live Swagger UI on each service port

---

*Document version: Phase 5 — API Testing Playground v0.1.0*
