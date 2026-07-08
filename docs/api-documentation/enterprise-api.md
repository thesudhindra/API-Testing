# Enterprise API Documentation

**Base URL:** `http://localhost:8082`  
**Authentication:** JWT Bearer from banking-api (`POST http://localhost:8081/v1/auth/login`)  
**OpenAPI:** `http://localhost:8082/swagger-ui.html`

**Required headers:**

| Header | Value |
|--------|-------|
| `Authorization` | `Bearer <accessToken>` |
| `X-Tenant-Id` | `tenant-demo` |
| `Content-Type` | `application/json` |

**RBAC summary:**

| Role | Access |
|------|--------|
| `RETAIL_CUSTOMER` | Own `party_id` resources only |
| `OPS_AGENT` / `ADMIN` | All parties + privileged endpoints |

---

## Status

### Get API health

`GET /health` — **No authentication required.**

### Get API version

`GET /version` — **No authentication required.**

## Operational endpoints

| Method | Path | Auth | Purpose |
|--------|------|------|---------|
| GET | `/actuator/health` | Public | Spring Actuator health |
| GET | `/v3/api-docs` | Public | OpenAPI spec |
| GET | `/swagger-ui.html` | Public | Swagger UI |

---

## Loans

### Create loan

`POST /v1/loans`

Party-scoped access.

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "accountId": "acct-customer-1",
  "productCode": "PERSONAL",
  "principal": 10000.00,
  "currency": "GBP",
  "interestRate": 5.5,
  "termMonths": 24
}
```

| Status code | Description |
|-------------|-------------|
| 201 Created | Loan created |
| 403 Forbidden | Wrong party |

---

### Get loan

`GET /v1/loans/{loanId}`

---

### List loans

`GET /v1/loans`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| partyId | string | query | Yes | Party ID |
| status | string | query | No | `ACTIVE`, `CLOSED`, `DEFAULTED` |
| page | integer | query | No | Default 0 |
| size | integer | query | No | Default 20 |

---

### Create loan repayment

`POST /v1/loans/{loanId}/repayments`

**Example request body:**

```json
{
  "amount": 500.00
}
```

| Status code | Description |
|-------------|-------------|
| 201 Created | Repayment recorded |

---

## Fixed Deposits

### Create fixed deposit

`POST /v1/fixed-deposits`

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "accountId": "acct-customer-1",
  "principal": 5000.00,
  "currency": "GBP",
  "interestRate": 4.25,
  "termDays": 365
}
```

---

### Get fixed deposit

`GET /v1/fixed-deposits/{depositId}`

---

### List fixed deposits

`GET /v1/fixed-deposits?partyId=party-customer-1`

---

## Recurring Deposits

### Create recurring deposit

`POST /v1/recurring-deposits`

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "accountId": "acct-customer-1",
  "installmentAmount": 200.00,
  "currency": "GBP",
  "frequency": "MONTHLY"
}
```

`frequency`: `MONTHLY` | `QUARTERLY`

---

### Get recurring deposit

`GET /v1/recurring-deposits/{depositId}`

---

### List recurring deposits

`GET /v1/recurring-deposits?partyId=party-customer-1`

---

### Post installment

`POST /v1/recurring-deposits/{depositId}/installments`

No request body. Records one installment payment.

| Status code | Description |
|-------------|-------------|
| 201 Created | Installment posted |

---

## Notifications

### Create notification

`POST /v1/notifications`

**Privileged:** `OPS_AGENT` or `ADMIN`

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "channel": "EMAIL",
  "subject": "Loan approved",
  "body": "Your loan application has been approved."
}
```

`channel`: `EMAIL` | `SMS` | `PUSH` | `IN_APP`

---

### List notifications

`GET /v1/notifications?partyId=party-customer-1`

---

### Mark notification read

`PATCH /v1/notifications/{notificationId}/read`

No request body.

---

## Documents

### Register document

`POST /v1/documents`

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "documentType": "PASSPORT",
  "fileName": "passport.pdf",
  "contentType": "application/pdf"
}
```

---

### Get document

`GET /v1/documents/{documentId}`

---

### List documents

`GET /v1/documents?partyId=party-customer-1`

---

## Reports

### Generate report

`POST /v1/reports`

**Privileged.** Submits background job.

**Example request body:**

```json
{
  "reportType": "LOAN_PORTFOLIO",
  "parameters": "partyId=party-customer-1"
}
```

| Status code | Description |
|-------------|-------------|
| 201 Created | Report job submitted |

---

### Get report

`GET /v1/reports/{reportId}`

Status: `PENDING` | `RUNNING` | `COMPLETED` | `FAILED`

---

### List reports

`GET /v1/reports`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| status | string | query | No | Filter by status |
| page | integer | query | No | Default 0 |
| size | integer | query | No | Default 20 |

---

## Fraud

### Screen for fraud

`POST /v1/fraud/screen`

Amount above tenant threshold (`5000`) creates an alert.

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "entityType": "PAYMENT",
  "entityId": "pay-lab-1",
  "amount": 6000.00
}
```

| Status code | Description |
|-------------|-------------|
| 201 Created | Alert created (amount > 5000) |
| 200 OK | Empty body — amount within threshold |
| 403 Forbidden | Party access denied |

**Example response (alert created):**

```json
{
  "id": "alert-uuid",
  "partyId": "party-customer-1",
  "status": "OPEN",
  "riskScore": 85,
  "ruleCode": "VELOCITY"
}
```

**What to test (scenario `fraud-velocity-alert`):** amount `6000` → `OPEN` alert, `riskScore` 85

---

### List fraud alerts

`GET /v1/fraud/alerts`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| partyId | string | query | No | If set, party-scoped; if omitted, privileged only |
| status | string | query | No | `OPEN`, `REVIEWED`, `DISMISSED` |
| page | integer | query | No | Default 0 |
| size | integer | query | No | Default 20 |

---

### Review fraud alert

`PATCH /v1/fraud/alerts/{alertId}/review`

**Privileged.**

**Example request body:**

```json
{
  "status": "REVIEWED"
}
```

---

## AML

### Create AML case

`POST /v1/aml/cases`

**Privileged.**

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "caseType": "SANCTIONS",
  "priority": "HIGH"
}
```

`caseType`: `SANCTIONS` | `PEP` | `TRANSACTION_MONITORING`  
`priority`: `LOW` | `MEDIUM` | `HIGH`

---

### Get AML case

`GET /v1/aml/cases/{caseId}`

---

### List AML cases

`GET /v1/aml/cases?partyId=party-customer-1`

---

### Submit AML screening

`POST /v1/aml/screenings`

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "screeningType": "SANCTIONS"
}
```

`screeningType`: `SANCTIONS` | `PEP`

---

## Admin

### List tenant settings

`GET /v1/admin/settings`

**Privileged.**

**Example response item:**

```json
{
  "settingKey": "fraud.velocity.threshold",
  "settingValue": "5000",
  "updatedBy": "user-admin",
  "updatedAt": "2026-01-01T00:00:00Z"
}
```

---

### Get setting by key

`GET /v1/admin/settings/{key}`

Example keys: `fraud.velocity.threshold`, `aml.auto_escalate_score`

---

### Update setting

`PUT /v1/admin/settings/{key}`

**Privileged.**

**Example request body:**

```json
{
  "settingValue": "6000"
}
```

**What to test (security lab):** Customer JWT → `403`

---

## Audit

### List audit events

`GET /v1/audit/events`

**Privileged.**

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| entityType | string | query | No | Filter by entity type |
| correlationId | string | query | No | Filter by correlation ID |
| from | datetime | query | No | ISO datetime |
| to | datetime | query | No | ISO datetime |
| page | integer | query | No | Default 0 |
| size | integer | query | No | Default 20 |

---

## Events

### List domain events

`GET /v1/events`

**Privileged.**

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| status | string | query | No | Event status filter |
| page | integer | query | No | Default 0 |
| size | integer | query | No | Default 20 |

---

## Jobs

### Submit background job

`POST /v1/jobs`

**Privileged.**

**Example request body:**

```json
{
  "jobType": "REPORT_GENERATION",
  "payload": "reportId=abc-123"
}
```

Supported types: `REPORT_GENERATION`, `AML_BATCH`

---

### Get job

`GET /v1/jobs/{jobId}`

---

### List jobs

`GET /v1/jobs`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| status | string | query | No | Job status filter |
| page | integer | query | No | Default 0 |
| size | integer | query | No | Default 20 |

---

## Scheduler

### List scheduled tasks

`GET /v1/scheduler/tasks`

Any authenticated user. Seed task: `sched-rd-reminder`

---

### Register scheduled task

`POST /v1/scheduler/tasks`

**Privileged.**

**Example request body:**

```json
{
  "taskType": "RD_INSTALLMENT_REMINDER",
  "cronExpression": "0 0 9 * * *",
  "payload": "{\"partyId\":\"party-customer-1\"}"
}
```

---

## Webhooks

### Register webhook

`POST /v1/webhooks`

**Privileged.**

**Example request body:**

```json
{
  "eventType": "FRAUD_ALERT_CREATED",
  "targetUrl": "http://localhost:8083/v1/mocks/webhooks/fail",
  "secret": "my-hmac-secret"
}
```

**What to test (scenario `webhook-retry-dlq`):**
1. Create failing mock on lab `:8083`
2. Register webhook pointing to mock URL
3. Trigger event → poll deliveries for retries → final `DLQ`

---

### List webhooks

`GET /v1/webhooks`

**Privileged.**

---

### List webhook deliveries

`GET /v1/webhooks/{webhookId}/deliveries`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| webhookId | string | path | Yes | Webhook ID |
| page | integer | query | No | Default 0 |
| size | integer | query | No | Default 20 |

---

## Circuit Breakers

### List circuit breakers

`GET /v1/resilience/circuit-breakers`

**Privileged.** Seed: `webhook-delivery` (CLOSED)

---

### Get circuit breaker

`GET /v1/resilience/circuit-breakers/{name}`

Example: `webhook-delivery`

---

## Testing Checklist (Enterprise API)

| # | Test case | Endpoint | Role | Expected |
|---|-----------|----------|------|----------|
| 1 | Health | `GET /health` | — | 200 |
| 2 | Create loan | `POST /v1/loans` | customer | 201 |
| 3 | Fraud screen high amount | `POST /v1/fraud/screen` | customer | 201 OPEN alert |
| 4 | Fraud screen low amount | amount 100 | customer | 200 empty |
| 5 | List admin settings | `GET /v1/admin/settings` | admin | 200 |
| 6 | Admin as customer | `GET /v1/admin/settings` | customer | 403 |
| 7 | Register webhook | `POST /v1/webhooks` | ops | 201 |
| 8 | AML screening | `POST /v1/aml/screenings` | customer | 201 |
| 9 | List circuit breakers | `GET /v1/resilience/circuit-breakers` | admin | 200 |
| 10 | Submit job | `POST /v1/jobs` | ops | 201 |

---

[← Back to index](README.md) · [Banking API ←](banking-api.md) · [Test Lab API →](playground-api.md)
