# Banking API Documentation

**Base URL:** `http://localhost:8081`  
**Authentication:** JWT Bearer (obtain via `POST /v1/auth/login`)  
**OpenAPI:** `http://localhost:8081/swagger-ui.html`

**Required headers on protected routes:**

| Header | Value |
|--------|-------|
| `Authorization` | `Bearer <accessToken>` |
| `X-Tenant-Id` | `tenant-demo` (recommended; must match JWT) |
| `Content-Type` | `application/json` (for POST/PATCH) |
| `Idempotency-Key` | UUID (required on payments, transfers, card authorizations, FX conversions) |

**Pagination:** List endpoints return `PageResponse`: `{ items, page, size, totalElements, totalPages, nextCursor }`. Default `page=0`, `size=20`.

---

## Status

### Get API health

`GET /health` — **No authentication required.**

| Status code | Description |
|-------------|-------------|
| 200 OK | `status: UP`, `service: banking-api` |
| 503 Service Unavailable | Database unhealthy |

---

### Get API version

`GET /version` — **No authentication required.**

---

## Operational endpoints

| Method | Path | Auth | Purpose |
|--------|------|------|---------|
| GET | `/actuator/health` | Public | Spring Actuator health |
| GET | `/v3/api-docs` | Public | OpenAPI spec (import to Postman) |
| GET | `/swagger-ui.html` | Public | Swagger UI |

---

## Authentication

### Login

`POST /v1/auth/login`

Issues a JWT access token. **No authentication required.**

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| tenantId | string | body | Yes | Tenant ID e.g. `tenant-demo` |
| username | string | body | Yes | Seed: `customer`, `ops`, or `admin` |
| password | string | body | Yes | Seed password: `password` |

**Example request body:**

```json
{
  "tenantId": "tenant-demo",
  "username": "customer",
  "password": "password"
}
```

| Status code | Description |
|-------------|-------------|
| 200 OK | Token issued |
| 401 Unauthorized | Invalid credentials |
| 422 Unprocessable Entity | Validation failed |

**Example response:**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

**What to test:** Save `accessToken` to Postman variable `jwt_token`. Invalid password → `401`.

---

### Get current user

`GET /v1/auth/me`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| Authorization | string | header | Yes | Bearer token |
| X-Tenant-Id | string | header | No | Tenant context |

| Status code | Description |
|-------------|-------------|
| 200 OK | User profile returned |
| 401 Unauthorized | Missing/invalid token |

**Example response (customer):**

```json
{
  "userId": "user-customer",
  "username": "customer",
  "tenantId": "tenant-demo",
  "partyId": "party-customer-1",
  "roles": ["RETAIL_CUSTOMER"]
}
```

---

## Roles

### List roles

`GET /v1/roles`

| Status code | Description |
|-------------|-------------|
| 200 OK | Roles returned |
| 401 Unauthorized | Missing token |

---

## Users

### List users

`GET /v1/users`

**Role required:** `OPS_AGENT` or `ADMIN`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| page | integer | query | No | Page index (default 0) |
| size | integer | query | No | Page size (default 20) |

| Status code | Description |
|-------------|-------------|
| 200 OK | User list |
| 403 Forbidden | Customer role denied |

---

### Get user by ID

`GET /v1/users/{userId}`

Self or privileged access.

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| userId | string | path | Yes | e.g. `user-customer` |

---

### Create user

`POST /v1/users`

**Role required:** `ADMIN`

**Example request body:**

```json
{
  "username": "newuser",
  "password": "password123",
  "partyId": "party-customer-1",
  "roleNames": ["RETAIL_CUSTOMER"]
}
```

| Status code | Description |
|-------------|-------------|
| 201 Created | User created |
| 409 Conflict | Username exists in tenant |

---

## Parties

### List parties

`GET /v1/parties`

**Role required:** `OPS_AGENT` or `ADMIN`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| status | string | query | No | `PROSPECT`, `ACTIVE`, `DORMANT`, `CLOSED` |
| page | integer | query | No | Default 0 |
| size | integer | query | No | Default 20 |

---

### Get party

`GET /v1/parties/{partyId}`

Own party or privileged.

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| partyId | string | path | Yes | e.g. `party-customer-1` |

| Status code | Description |
|-------------|-------------|
| 200 OK | Party returned |
| 403 Forbidden | BOLA — customer accessing other party |
| 404 Not Found | Party not found |

**What to test (BOLA):** Customer token → `GET /v1/parties/party-other` → `403`

---

### Create party

`POST /v1/parties`

**Role required:** `OPS_AGENT` or `ADMIN`

**Example request body:**

```json
{
  "partyType": "INDIVIDUAL",
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com"
}
```

`partyType`: `INDIVIDUAL` | `CORPORATE` | `JOINT`

---

### Update party

`PATCH /v1/parties/{partyId}`

**Example request body:**

```json
{
  "firstName": "Jane",
  "email": "jane.updated@example.com"
}
```

Customers cannot change `status` field → `403`.

---

## KYC

### List KYC cases

`GET /v1/kyc-cases`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| partyId | string | query | Yes | Party to list cases for |

---

### Get KYC case

`GET /v1/kyc-cases/{caseId}`

Seed case: `kyc-1`

---

### Submit KYC case

`POST /v1/kyc-cases`

**Example request body:**

```json
{
  "partyId": "party-customer-1"
}
```

| Status code | Description |
|-------------|-------------|
| 201 Created | Case created |
| 409 Conflict | Open case already exists |

---

### Review KYC case

`POST /v1/kyc-cases/{caseId}/review`

**Role required:** `OPS_AGENT` or `ADMIN`

**Example request body:**

```json
{
  "status": "APPROVED",
  "decisionReason": "Documents verified"
}
```

`status`: `OPEN` | `IN_REVIEW` | `PENDING_DOCUMENTS` | `APPROVED` | `REJECTED`

---

## Accounts

### List accounts

`GET /v1/accounts`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| partyId | string | query | Yes | e.g. `party-customer-1` |

---

### Get account

`GET /v1/accounts/{accountId}`

Seed IDs: `acct-customer-1`, `acct-customer-2`, `acct-customer-eur`

---

### Open account

`POST /v1/accounts`

Requires approved KYC for party.

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "currency": "GBP",
  "productCode": "CURRENT"
}
```

| Status code | Description |
|-------------|-------------|
| 201 Created | Account opened |
| 400 Bad Request | KYC not approved |

---

## Beneficiaries

### List beneficiaries

`GET /v1/beneficiaries?partyId=party-customer-1`

---

### Add beneficiary

`POST /v1/beneficiaries`

**Example request body:**

```json
{
  "partyId": "party-customer-1",
  "nickname": "Landlord",
  "sortCode": "11-22-33",
  "accountNumber": "12345678"
}
```

New beneficiaries start as `PENDING_VERIFICATION`. Use seed `ben-1` (`ACTIVE`) for payment tests.

---

### Delete beneficiary

`DELETE /v1/beneficiaries/{beneficiaryId}`

| Status code | Description |
|-------------|-------------|
| 204 No Content | Deleted |

---

## Payments

### Create payment

`POST /v1/payments`

**Requires `Idempotency-Key` header.**

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| Idempotency-Key | string | header | Yes | Unique per logical operation |
| partyId | string | body | Yes | `party-customer-1` |
| accountId | string | body | Yes | `acct-customer-1` |
| beneficiaryId | string | body | Yes | `ben-1` |
| amount | number | body | Yes | Min 0.01 |
| currency | string | body | Yes | 3-letter uppercase e.g. `GBP` |
| reference | string | body | No | Max 128 chars |

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

| Status code | Description |
|-------------|-------------|
| 201 Created | Payment completed |
| 400 Bad Request | Insufficient funds, inactive beneficiary |
| 409 Conflict | Same idempotency key, different body |
| 401 Unauthorized | Missing token |

**What to test:**
- Happy path → `201`, status `COMPLETED`
- Replay same key + body → `201`, no double debit
- Same key, different amount → `409`
- Amount 999999 → `400` Insufficient funds

---

### Get payment

`GET /v1/payments/{paymentId}`

---

### List payments

`GET /v1/payments?partyId=party-customer-1`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| partyId | string | query | Yes | Party ID |
| status | string | query | No | `PENDING`, `COMPLETED`, `FAILED`, etc. |
| page | integer | query | No | Default 0 |
| size | integer | query | No | Default 20 |

---

## Transfers

### Create transfer

`POST /v1/transfers`

**Requires `Idempotency-Key` header.**

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

| Status code | Description |
|-------------|-------------|
| 201 Created | Transfer completed |
| 400 Bad Request | Same account, currency mismatch, insufficient funds |
| 409 Conflict | Optimistic locking / idempotency conflict |

**What to test (concurrency):** 5 parallel transfers → at least one `409`

---

### Get transfer

`GET /v1/transfers/{transferId}`

---

### List transfers

`GET /v1/transfers?accountId=acct-customer-1`

---

## Cards

### Issue card

`POST /v1/cards`

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

`GET /v1/cards?partyId=party-customer-1`

Seed card: `card-1`

---

### Get card

`GET /v1/cards/{cardId}`

---

### Authorize card transaction

`POST /v1/cards/{cardId}/authorizations`

**Requires `Idempotency-Key` header.**

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

### Create FX quote

`POST /v1/fx/quotes`

**Example request body:**

```json
{
  "fromCurrency": "GBP",
  "toCurrency": "EUR",
  "fromAmount": 100.00
}
```

**Seeded rates:** GBP→EUR `1.17`, EUR→GBP `0.85`, GBP→USD `1.27`, USD→GBP `0.79`

Quotes expire after 5 minutes.

---

### Execute FX conversion

`POST /v1/fx/conversions`

**Requires `Idempotency-Key` header.**

**Example request body:**

```json
{
  "quoteId": "<from quote response>",
  "fromAccountId": "acct-customer-1",
  "toAccountId": "acct-customer-eur",
  "partyId": "party-customer-1"
}
```

---

### Get conversion

`GET /v1/fx/conversions/{conversionId}`

---

## Transactions

### List transactions

`GET /v1/transactions`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| accountId | string | query | Yes | Account to query |
| type | string | query | No | `PAYMENT`, `TRANSFER`, etc. |
| status | string | query | No | Transaction status filter |
| from | date | query | No | ISO date `YYYY-MM-DD` |
| to | date | query | No | ISO date |
| page | integer | query | No | Default 0 |
| size | integer | query | No | Default 20 |

---

### Get transaction

`GET /v1/transactions/{transactionId}`

---

## Ledger

### List ledger entries

`GET /v1/ledger-entries`

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| accountId | string | query | Yes | Account ID |
| from | date | query | No | Start date |
| to | date | query | No | End date |
| page | integer | query | No | Default 0 |
| size | integer | query | No | Default 20 |

---

## Statements

### Generate statement

`POST /v1/accounts/{accountId}/statements`

**Example request body:**

```json
{
  "periodStart": "2026-01-01",
  "periodEnd": "2026-01-31"
}
```

| Status code | Description |
|-------------|-------------|
| 201 Created | Statement generated |

---

### List statements for account

`GET /v1/accounts/{accountId}/statements`

---

### Get statement

`GET /v1/statements/{statementId}`

---

## Testing Checklist (Banking API)

| # | Test case | Endpoint | Expected |
|---|-----------|----------|----------|
| 1 | Health check | `GET /health` | 200 UP |
| 2 | Login customer | `POST /v1/auth/login` | 200 + token |
| 3 | Me endpoint | `GET /v1/auth/me` | partyId matches |
| 4 | List own accounts | `GET /v1/accounts?partyId=...` | 200 |
| 5 | Payment happy path | `POST /v1/payments` | 201 COMPLETED |
| 6 | Idempotency replay | Same payment + key | 201, single debit |
| 7 | Transfer | `POST /v1/transfers` | 201 |
| 8 | BOLA negative | `GET /v1/parties/party-other` | 403 |
| 9 | No auth | `GET /v1/accounts` | 401 |
| 10 | Wrong tenant header | Mismatched X-Tenant-Id | 403 |

---

[← Back to index](README.md) · [Enterprise API →](enterprise-api.md)
