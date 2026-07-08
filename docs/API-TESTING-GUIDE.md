# API Testing Playground — API Documentation

This platform allows SDET engineers and students to test banking APIs on localhost. You can log in, make payments, run fraud checks, and use the Test Lab to reset data and run practice scenarios.

**Start the stack:** `make up-full`

| API | Base URL |
|-----|----------|
| Platform | http://localhost:8080 |
| Banking | http://localhost:8081 |
| Enterprise | http://localhost:8082 |
| Test Lab | http://localhost:8083 |

**Postman environment file:** `docs/postman/playground-environment.json`

---

## Endpoints

**Platform API** — http://localhost:8080

- Status — Get health, Get version
- Demo — Get bad request error, Get not found error, Get internal error, Validate body

**Banking API** — http://localhost:8081

- Status — Get health, Get version
- Authentication — Login, Get current user
- Roles — List roles
- Users — List users, Get user, Create user
- Parties — List parties, Get party, Create party, Update party
- KYC — List cases, Get case, Submit case, Review case
- Accounts — List accounts, Get account, Open account
- Beneficiaries — List beneficiaries, Add beneficiary, Delete beneficiary
- Payments — Create payment, Get payment, List payments
- Transfers — Create transfer, Get transfer, List transfers
- Cards — Issue card, List cards, Get card, Authorize card
- FX — Create quote, Execute conversion, Get conversion
- Transactions — List transactions, Get transaction
- Ledger — List ledger entries
- Statements — Generate statement, List statements, Get statement

**Enterprise API** — http://localhost:8082

- Status — Get health, Get version
- Loans — Create loan, Get loan, List loans, Create repayment
- Fixed Deposits — Create, Get, List
- Recurring Deposits — Create, Get, List, Post installment
- Notifications — Create, List, Mark read
- Documents — Register, Get, List
- Reports — Generate, Get, List
- Fraud — Screen, List alerts, Review alert
- AML — Create case, Get case, List cases, Submit screening
- Admin — List settings, Get setting, Update setting
- Audit — List audit events
- Events — List events
- Jobs — Submit job, Get job, List jobs
- Scheduler — List tasks, Register task
- Webhooks — Register, List, List deliveries
- Circuit Breakers — List, Get by name

**Test Lab API** — http://localhost:8083

- Status — Get health, Get version
- Scenarios — List, Get by slug, Start run, Complete run
- Contracts — List, Get file
- Configuration — List, Get key, Update key
- Faults — List, Create, Disable
- Mocks — List, Create, Serve mock (public)
- Test Data — Generate, List, Delete namespace
- Seed — Generate by profile
- Reset — Reset environment
- Dashboard — Get overview
- Performance — List load profiles
- Security — List test cases
- Concurrency — List scenarios, Get race profile
- Failures — List simulations, Get simulation

---

# API Authentication

## Platform API & Test Lab — Basic Auth

Username: `learner`  
Password: `learner`

In Postman: **Authorization → Basic Auth** → enter username and password.

Example header:
```
Authorization: Basic bGVhcm5lcjpsZWFybmVy
```

## Banking API & Enterprise API — JWT Bearer

**Step 1 — Login (no auth required):**

POST http://localhost:8081/v1/auth/login

```json
{
  "tenantId": "tenant-demo",
  "username": "customer",
  "password": "password"
}
```

**Step 2 — Copy `accessToken` from the response.**

**Step 3 — Use on all banking and enterprise calls:**

```
Authorization: Bearer YOUR_TOKEN_HERE
X-Tenant-Id: tenant-demo
```

**Test users (password `password` for all):**

| Username | Role | Use for |
|----------|------|---------|
| customer | Retail customer | Happy paths, own data |
| ops | Operations | Admin reads, KYC review |
| admin | Administrator | Settings, webhooks, users |

---

# Seed data (use in Postman)

| What | ID |
|------|-----|
| Tenant | tenant-demo |
| Party (Jane Doe) | party-customer-1 |
| GBP current account (£5,000) | acct-customer-1 |
| GBP savings account (£1,000) | acct-customer-2 |
| EUR account (€200) | acct-customer-eur |
| Beneficiary (active) | ben-1 |
| Card (debit, last4 4242) | card-1 |
| Fraud threshold | 5000 |

**Before each test session:** reset the lab:

POST http://localhost:8083/v1/playground/reset

```json
{ "scope": "ALL" }
```

Auth: Basic `learner` / `learner`

---

# Platform API

Base URL: **http://localhost:8080**

---

## Status

### Get health

GET /health

Returns the status of the API. No authentication required.

**Parameters**

| Name | Type | In | Required | Description |
|------|------|-----|----------|-------------|
| — | — | — | — | No parameters |

**Status codes**

| Status code | Description |
|-------------|-------------|
| 200 OK | API is running |
| 503 Service Unavailable | Database is down |

**Example response:**

```json
{
  "status": "UP",
  "service": "platform-api",
  "components": { "database": "UP" }
}
```

---

### Get version

GET /version

Returns version information. No authentication required.

**Parameters**

No parameters.

**Status codes**

| Status code | Description |
|-------------|-------------|
| 200 OK | Success |

**Example response:**

```json
{
  "name": "platform-api",
  "version": "0.1.0-SNAPSHOT"
}
```

---

## Demo

All demo endpoints require **Basic Auth** (`learner` / `learner`).

### Get bad request error

GET /v1/demo/errors/bad-request

Returns a sample 400 error in RFC 7807 format.

**Parameters**

| Name | Type | In | Required | Description |
|------|------|-----|----------|-------------|
| Authorization | string | header | Yes | Basic auth |

**Status codes**

| Status code | Description |
|-------------|-------------|
| 400 Bad Request | Demo error returned |
| 401 Unauthorized | Missing auth |

---

### Get not found error

GET /v1/demo/errors/not-found

Returns a sample 404 error.

**Status codes**

| Status code | Description |
|-------------|-------------|
| 404 Not Found | Demo error returned |
| 401 Unauthorized | Missing auth |

---

### Get internal server error

GET /v1/demo/errors/internal

Returns a sample 500 error.

**Status codes**

| Status code | Description |
|-------------|-------------|
| 500 Internal Server Error | Demo error returned |
| 401 Unauthorized | Missing auth |

---

### Validate request body

POST /v1/demo/validate

The request body must be in JSON format. Tests validation errors.

**Parameters**

| Name | Type | In | Required | Description |
|------|------|-----|----------|-------------|
| Authorization | string | header | Yes | Basic auth |
| name | string | body | Yes | 2–50 characters |

**Example request body:**

```json
{
  "name": "Jane"
}
```

**Status codes**

| Status code | Description |
|-------------|-------------|
| 200 OK | Validation passed |
| 401 Unauthorized | Missing auth |
| 422 Unprocessable Entity | Validation failed |

---

# Banking API

Base URL: **http://localhost:8081**  
Auth: **Bearer token** from login (see Authentication above)

**Extra headers for money operations:**

```
Idempotency-Key: any-unique-string-per-operation
```

Required on: POST /v1/payments, POST /v1/transfers, POST /v1/cards/{id}/authorizations, POST /v1/fx/conversions

---

## Status

### Get health

GET /health

No authentication required.

**Example response:**

```json
{
  "status": "UP",
  "service": "banking-api"
}
```

---

### Get version

GET /version

No authentication required.

---

## Authentication

### Login

POST /v1/auth/login

No authentication required. Returns a JWT token.

**Parameters**

| Name | Type | In | Required | Description |
|------|------|-----|----------|-------------|
| tenantId | string | body | Yes | tenant-demo |
| username | string | body | Yes | customer, ops, or admin |
| password | string | body | Yes | password |

**Example request body:**

```json
{
  "tenantId": "tenant-demo",
  "username": "customer",
  "password": "password"
}
```

**Status codes**

| Status code | Description |
|-------------|-------------|
| 200 OK | Token returned |
| 401 Unauthorized | Wrong credentials |
| 422 Unprocessable Entity | Invalid body |

**Example response:**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

### Get current user

GET /v1/auth/me

**Parameters**

| Name | Type | In | Required | Description |
|------|------|-----|----------|-------------|
| Authorization | string | header | Yes | Bearer token |
| X-Tenant-Id | string | header | No | tenant-demo |

**Status codes**

| Status code | Description |
|-------------|-------------|
| 200 OK | User profile returned |
| 401 Unauthorized | Missing or invalid token |

**Example response:**

```json
{
  "username": "customer",
  "partyId": "party-customer-1",
  "roles": ["RETAIL_CUSTOMER"]
}
```

---

## Payments

### Create payment

POST /v1/payments

Creates a payment from an account to a beneficiary. **Requires Idempotency-Key header.**

**Parameters**

| Name | Type | In | Required | Description |
|------|------|-----|----------|-------------|
| Authorization | string | header | Yes | Bearer token |
| X-Tenant-Id | string | header | No | tenant-demo |
| Idempotency-Key | string | header | Yes | Unique key per payment |
| partyId | string | body | Yes | party-customer-1 |
| accountId | string | body | Yes | acct-customer-1 |
| beneficiaryId | string | body | Yes | ben-1 |
| amount | number | body | Yes | e.g. 25.50 |
| currency | string | body | Yes | GBP |
| reference | string | body | No | Payment reference |

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "accountId": "acct-customer-1",
  "beneficiaryId": "ben-1",
  "amount": 25.50,
  "currency": "GBP",
  "reference": "rent"
}
```

**Status codes**

| Status code | Description |
|-------------|-------------|
| 201 Created | Payment completed |
| 400 Bad Request | Insufficient funds |
| 401 Unauthorized | Missing token |
| 409 Conflict | Same idempotency key, different body |

**What to test:** Send the same request twice with the same Idempotency-Key — balance should only decrease once.

---

### Get payment

GET /v1/payments/{paymentId}

**Parameters**

| Name | Type | In | Required | Description |
|------|------|-----|----------|-------------|
| paymentId | string | path | Yes | Payment ID from create response |
| Authorization | string | header | Yes | Bearer token |

**Status codes**

| Status code | Description |
|-------------|-------------|
| 200 OK | Payment found |
| 404 Not Found | Payment does not exist |

---

### List payments

GET /v1/payments?partyId=party-customer-1

**Parameters**

| Name | Type | In | Required | Description |
|------|------|-----|----------|-------------|
| partyId | string | query | Yes | Party ID |
| status | string | query | No | Filter by status |
| page | integer | query | No | Default 0 |
| size | integer | query | No | Default 20 |

**Status codes**

| Status code | Description |
|-------------|-------------|
| 200 OK | List returned |

---

## Transfers

### Create transfer

POST /v1/transfers

Moves money between two accounts. **Requires Idempotency-Key header.**

**Example request body:**

```json
{
  "fromAccountId": "acct-customer-1",
  "toAccountId": "acct-customer-2",
  "amount": 50.00,
  "currency": "GBP",
  "reference": "savings"
}
```

**Status codes**

| Status code | Description |
|-------------|-------------|
| 201 Created | Transfer completed |
| 409 Conflict | Concurrent update conflict |

---

### Get transfer

GET /v1/transfers/{transferId}

---

### List transfers

GET /v1/transfers?accountId=acct-customer-1

---

## Accounts

### List accounts

GET /v1/accounts?partyId=party-customer-1

---

### Get account

GET /v1/accounts/acct-customer-1

---

### Open account

POST /v1/accounts

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "currency": "GBP",
  "productCode": "CURRENT"
}
```

---

## Parties

### Get party

GET /v1/parties/party-customer-1

**What to test (security):** Login as customer, then GET /v1/parties/party-other → expect **403 Forbidden**.

---

### List parties

GET /v1/parties

Ops or admin only.

---

### Create party

POST /v1/parties

Ops or admin only.

**Example request body:**

```json
{
  "partyType": "INDIVIDUAL",
  "firstName": "John",
  "lastName": "Smith",
  "email": "john@example.com"
}
```

---

### Update party

PATCH /v1/parties/party-customer-1

**Example request body:**

```json
{
  "firstName": "Jane",
  "email": "jane.updated@example.com"
}
```

---

## Beneficiaries

### List beneficiaries

GET /v1/beneficiaries?partyId=party-customer-1

---

### Add beneficiary

POST /v1/beneficiaries

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "nickname": "Landlord",
  "sortCode": "11-22-33",
  "accountNumber": "12345678"
}
```

---

### Delete beneficiary

DELETE /v1/beneficiaries/{beneficiaryId}

**Status codes**

| Status code | Description |
|-------------|-------------|
| 204 No Content | Deleted |

---

## Cards

### Issue card

POST /v1/cards

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "accountId": "acct-customer-1",
  "productCode": "DEBIT"
}
```

---

### List cards

GET /v1/cards?partyId=party-customer-1

---

### Get card

GET /v1/cards/card-1

---

### Authorize card transaction

POST /v1/cards/card-1/authorizations

**Requires Idempotency-Key header.**

**Example request body:**

```json
{
  "merchantName": "Coffee Shop",
  "amount": 4.50,
  "currency": "GBP"
}
```

---

## FX

### Create quote

POST /v1/fx/quotes

**Example request body:**

```json
{
  "fromCurrency": "GBP",
  "toCurrency": "EUR",
  "fromAmount": 100.00
}
```

Save `quoteId` from the response.

---

### Execute conversion

POST /v1/fx/conversions

**Requires Idempotency-Key header.**

**Example request body:**

```json
{
  "quoteId": "paste-quote-id-here",
  "fromAccountId": "acct-customer-1",
  "toAccountId": "acct-customer-eur",
  "partyId": "party-customer-1"
}
```

---

### Get conversion

GET /v1/fx/conversions/{conversionId}

---

## Transactions

### List transactions

GET /v1/transactions?accountId=acct-customer-1

Optional filters: `type`, `status`, `from`, `to`, `page`, `size`

---

### Get transaction

GET /v1/transactions/{transactionId}

---

## Ledger

### List ledger entries

GET /v1/ledger-entries?accountId=acct-customer-1

Optional: `from`, `to`, `page`, `size`

---

## Statements

### Generate statement

POST /v1/accounts/acct-customer-1/statements

**Example request body:**

```json
{
  "periodStart": "2026-01-01",
  "periodEnd": "2026-01-31"
}
```

---

### List statements

GET /v1/accounts/acct-customer-1/statements

---

### Get statement

GET /v1/statements/{statementId}

---

## KYC

### List KYC cases

GET /v1/kyc-cases?partyId=party-customer-1

---

### Get KYC case

GET /v1/kyc-cases/kyc-1

---

### Submit KYC case

POST /v1/kyc-cases

```json
{ "partyId": "party-customer-1" }
```

---

### Review KYC case

POST /v1/kyc-cases/{caseId}/review

Ops or admin only.

```json
{
  "status": "APPROVED",
  "decisionReason": "Documents verified"
}
```

---

## Users & Roles

### List roles

GET /v1/roles

---

### List users

GET /v1/users

Ops or admin only.

---

### Get user

GET /v1/users/user-customer

---

### Create user

POST /v1/users

Admin only.

```json
{
  "username": "newuser",
  "password": "password123",
  "partyId": "party-customer-1",
  "roleNames": ["RETAIL_CUSTOMER"]
}
```

---

# Enterprise API

Base URL: **http://localhost:8082**  
Auth: **Same Bearer token** from banking login

---

## Status

### Get health

GET /health — No auth.

### Get version

GET /version — No auth.

---

## Fraud

### Screen for fraud

POST /v1/fraud/screen

Amount above **5000** creates a fraud alert.

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "entityType": "PAYMENT",
  "entityId": "pay-test-1",
  "amount": 6000.00
}
```

**Status codes**

| Status code | Description |
|-------------|-------------|
| 201 Created | Alert created (amount > 5000) |
| 200 OK | No alert (amount within limit) |

**Example response (alert):**

```json
{
  "status": "OPEN",
  "riskScore": 85,
  "ruleCode": "VELOCITY"
}
```

---

### List fraud alerts

GET /v1/fraud/alerts?partyId=party-customer-1

---

### Review fraud alert

PATCH /v1/fraud/alerts/{alertId}/review

Ops or admin only.

```json
{ "status": "REVIEWED" }
```

---

## Loans

### Create loan

POST /v1/loans

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

---

### Get loan

GET /v1/loans/{loanId}

---

### List loans

GET /v1/loans?partyId=party-customer-1

---

### Create repayment

POST /v1/loans/{loanId}/repayments

```json
{ "amount": 500.00 }
```

---

## Fixed Deposits

### Create fixed deposit

POST /v1/fixed-deposits

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

GET /v1/fixed-deposits/{depositId}

---

### List fixed deposits

GET /v1/fixed-deposits?partyId=party-customer-1

---

## Recurring Deposits

### Create recurring deposit

POST /v1/recurring-deposits

```json
{
  "partyId": "party-customer-1",
  "accountId": "acct-customer-1",
  "installmentAmount": 200.00,
  "currency": "GBP",
  "frequency": "MONTHLY"
}
```

---

### Get recurring deposit

GET /v1/recurring-deposits/{depositId}

---

### List recurring deposits

GET /v1/recurring-deposits?partyId=party-customer-1

---

### Post installment

POST /v1/recurring-deposits/{depositId}/installments

No body.

---

## Notifications

### Create notification

POST /v1/notifications

Ops or admin only.

```json
{
  "partyId": "party-customer-1",
  "channel": "EMAIL",
  "subject": "Loan approved",
  "body": "Your loan has been approved."
}
```

---

### List notifications

GET /v1/notifications?partyId=party-customer-1

---

### Mark notification read

PATCH /v1/notifications/{notificationId}/read

---

## Documents

### Register document

POST /v1/documents

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

GET /v1/documents/{documentId}

---

### List documents

GET /v1/documents?partyId=party-customer-1

---

## Reports

### Generate report

POST /v1/reports

Ops or admin only.

```json
{
  "reportType": "LOAN_PORTFOLIO",
  "parameters": "partyId=party-customer-1"
}
```

---

### Get report

GET /v1/reports/{reportId}

---

### List reports

GET /v1/reports

---

## AML

### Create AML case

POST /v1/aml/cases

Ops or admin only.

```json
{
  "partyId": "party-customer-1",
  "caseType": "SANCTIONS",
  "priority": "HIGH"
}
```

---

### Get AML case

GET /v1/aml/cases/{caseId}

---

### List AML cases

GET /v1/aml/cases?partyId=party-customer-1

---

### Submit AML screening

POST /v1/aml/screenings

```json
{
  "partyId": "party-customer-1",
  "screeningType": "SANCTIONS"
}
```

---

## Admin

### List settings

GET /v1/admin/settings

Ops or admin only.

---

### Get setting

GET /v1/admin/settings/fraud.velocity.threshold

---

### Update setting

PUT /v1/admin/settings/fraud.velocity.threshold

Ops or admin only.

```json
{ "settingValue": "6000" }
```

---

## Audit

### List audit events

GET /v1/audit/events

Ops or admin only. Optional: `entityType`, `correlationId`, `from`, `to`

---

## Events

### List domain events

GET /v1/events

Ops or admin only.

---

## Jobs

### Submit job

POST /v1/jobs

```json
{
  "jobType": "REPORT_GENERATION",
  "payload": "reportId=abc"
}
```

---

### Get job

GET /v1/jobs/{jobId}

---

### List jobs

GET /v1/jobs

---

## Scheduler

### List scheduled tasks

GET /v1/scheduler/tasks

---

### Register task

POST /v1/scheduler/tasks

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

POST /v1/webhooks

Ops or admin only.

```json
{
  "eventType": "FRAUD_ALERT_CREATED",
  "targetUrl": "http://localhost:8083/v1/mocks/webhooks/fail",
  "secret": "my-secret"
}
```

Create the failing mock on the Test Lab first (see Mocks section).

---

### List webhooks

GET /v1/webhooks

---

### List webhook deliveries

GET /v1/webhooks/{webhookId}/deliveries

---

## Circuit Breakers

### List circuit breakers

GET /v1/resilience/circuit-breakers

Seed: `webhook-delivery` (CLOSED)

---

### Get circuit breaker

GET /v1/resilience/circuit-breakers/webhook-delivery

---

# Test Lab API

Base URL: **http://localhost:8083**  
Auth: **Basic** `learner` / `learner` (except public mocks)

---

## Status

### Get health

GET /health — No auth.

### Get version

GET /version — No auth.

---

## Reset

### Reset environment

POST /v1/playground/reset

Run this **before every test session**.

**Example request body:**

```json
{
  "scope": "ALL"
}
```

`scope` can be: `PLAYGROUND`, `BANKING`, `ENTERPRISE`, or `ALL`

**Status codes**

| Status code | Description |
|-------------|-------------|
| 200 OK | Reset completed |

---

## Scenarios

### List scenarios

GET /v1/scenarios

Returns 6 practice scenarios.

---

### Get scenario

GET /v1/scenarios/payment-happy-path

Other slugs: `idempotency-replay`, `fraud-velocity-alert`, `webhook-retry-dlq`, `concurrent-transfer-race`, `bola-party-access`

---

### Start scenario run

POST /v1/scenarios/payment-happy-path/runs

No body. Save the `id` from the response.

---

### Complete scenario run

PATCH /v1/scenarios/runs/{runId}

```json
{ "status": "COMPLETED" }
```

---

## Mocks

### List mocks

GET /v1/playground/mocks

---

### Create mock

POST /v1/playground/mocks

```json
{
  "path": "/v1/mocks/webhooks/fail",
  "httpMethod": "POST",
  "statusCode": 500,
  "responseBody": "{\"error\":\"fail\"}",
  "delayMs": 0,
  "enabled": true
}
```

---

### Call mock (public — no auth)

POST /v1/mocks/aml/screen/clear

```json
{}
```

**Example response:**

```json
{
  "result": "CLEAR",
  "matchScore": 0
}
```

Pre-built mocks:
- POST /v1/mocks/aml/screen/clear → CLEAR
- POST /v1/mocks/aml/screen/review → REVIEW

---

## Test Data

### Generate test data

POST /v1/playground/test-data

```json
{
  "namespace": "my-test-session",
  "profile": "retail-customer"
}
```

Profiles: `retail-customer`, `high-balance`, `aml-ready`

---

### List test data

GET /v1/playground/test-data?namespace=my-test-session

---

### Delete test data

DELETE /v1/playground/test-data/my-test-session

---

## Seed Generator

### Generate seed

POST /v1/playground/seed

```json
{ "profile": "retail-customer" }
```

---

## Fault Injection

### List faults

GET /v1/playground/faults

---

### Create fault

POST /v1/playground/faults

```json
{
  "targetService": "BANKING",
  "pathPattern": "/v1/payments",
  "faultType": "LATENCY",
  "config": { "delayMs": 500 }
}
```

---

### Disable fault

DELETE /v1/playground/faults/{id}

---

## Dashboard

### Get dashboard

GET /v1/playground/dashboard

Shows health of all 4 APIs, scenario count, recent runs.

---

## Contracts

### List contracts

GET /v1/contracts

---

### Get contract file

GET /v1/contracts/banking-api/openapi.yaml

---

## Configuration

### List config

GET /v1/playground/config

---

### Update config

PUT /v1/playground/config/default_tenant

```json
{ "value": "tenant-demo" }
```

---

## Performance / Security / Concurrency / Failures

### List load test profiles

GET /v1/playground/performance/profiles

---

### List security test cases

GET /v1/playground/security/test-cases

Run each case against banking or enterprise API.

---

### List concurrency scenarios

GET /v1/playground/concurrency/scenarios

---

### Get race profile

GET /v1/playground/concurrency/scenarios/concurrent-transfer-race/race-profile

Use Postman Collection Runner with 5 iterations.

---

### List failure simulations

GET /v1/playground/failures

---

### Get failure simulation

GET /v1/playground/failures/banking-latency-spike

---

# Quick test flow (copy into Postman)

1. **Reset** — POST http://localhost:8083/v1/playground/reset → `{"scope":"ALL"}` (Basic auth)
2. **Login** — POST http://localhost:8081/v1/auth/login → save token
3. **Payment** — POST http://localhost:8081/v1/payments (Bearer + Idempotency-Key)
4. **Fraud** — POST http://localhost:8082/v1/fraud/screen → amount 6000
5. **Dashboard** — GET http://localhost:8083/v1/playground/dashboard

---

*API Testing Playground — v0.1.0*
