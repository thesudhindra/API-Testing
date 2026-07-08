# API Test Scenarios — Complete Reference (138 scenarios)

One test scenario per section. Copy each block into Postman.

**Prerequisites:** `make up-full` running. Import `docs/postman/playground-environment.json` and `docs/postman/playground-api-collection.json`.

**Postman variables:** `jwt_token`, `admin_jwt`, `idempotency_key`

---

## Scenario Index

- **Scenario 001** — Platform — Get health
- **Scenario 002** — Platform — Get version
- **Scenario 003** — Platform — Demo bad request error
- **Scenario 004** — Platform — Demo not found error
- **Scenario 005** — Platform — Demo internal error
- **Scenario 006** — Platform — Validate body (valid)
- **Scenario 007** — Platform — Validate body (invalid)
- **Scenario 008** — Banking — Login as customer
- **Scenario 009** — Banking — Login as ops
- **Scenario 010** — Banking — Login as admin
- **Scenario 011** — Banking — Get current user
- **Scenario 012** — Banking — Get health
- **Scenario 013** — Banking — Get version
- **Scenario 014** — Banking — List roles
- **Scenario 015** — Banking — List users
- **Scenario 016** — Banking — Get user by id
- **Scenario 017** — Banking — Create user
- **Scenario 018** — Banking — Get party
- **Scenario 019** — Banking — List parties
- **Scenario 020** — Banking — Create party
- **Scenario 021** — Banking — Update party
- **Scenario 022** — Banking — BOLA get other party (negative)
- **Scenario 023** — Banking — List KYC cases
- **Scenario 024** — Banking — Get KYC case
- **Scenario 025** — Banking — Submit KYC case
- **Scenario 026** — Banking — Review KYC case
- **Scenario 027** — Banking — List accounts
- **Scenario 028** — Banking — Get account
- **Scenario 029** — Banking — Open account
- **Scenario 030** — Banking — List beneficiaries
- **Scenario 031** — Banking — Add beneficiary
- **Scenario 032** — Banking — Delete beneficiary
- **Scenario 033** — Banking — Create payment
- **Scenario 034** — Banking — Get payment
- **Scenario 035** — Banking — List payments
- **Scenario 036** — Banking — Payment without auth (negative)
- **Scenario 037** — Banking — Create transfer
- **Scenario 038** — Banking — Get transfer
- **Scenario 039** — Banking — List transfers
- **Scenario 040** — Banking — Issue card
- **Scenario 041** — Banking — List cards
- **Scenario 042** — Banking — Get card
- **Scenario 043** — Banking — Authorize card
- **Scenario 044** — Banking — Create FX quote
- **Scenario 045** — Banking — Execute FX conversion
- **Scenario 046** — Banking — Get FX conversion
- **Scenario 047** — Banking — List transactions
- **Scenario 048** — Banking — Get transaction
- **Scenario 049** — Banking — List ledger entries
- **Scenario 050** — Banking — Generate statement
- **Scenario 051** — Banking — List statements
- **Scenario 052** — Banking — Get statement
- **Scenario 053** — Enterprise — Get health
- **Scenario 054** — Enterprise — Get version
- **Scenario 055** — Enterprise — Create loan
- **Scenario 056** — Enterprise — Get loan
- **Scenario 057** — Enterprise — List loans
- **Scenario 058** — Enterprise — Create loan repayment
- **Scenario 059** — Enterprise — Create fixed deposit
- **Scenario 060** — Enterprise — Get fixed deposit
- **Scenario 061** — Enterprise — List fixed deposits
- **Scenario 062** — Enterprise — Create recurring deposit
- **Scenario 063** — Enterprise — Get recurring deposit
- **Scenario 064** — Enterprise — List recurring deposits
- **Scenario 065** — Enterprise — Post RD installment
- **Scenario 066** — Enterprise — Create notification
- **Scenario 067** — Enterprise — List notifications
- **Scenario 068** — Enterprise — Mark notification read
- **Scenario 069** — Enterprise — Register document
- **Scenario 070** — Enterprise — Get document
- **Scenario 071** — Enterprise — List documents
- **Scenario 072** — Enterprise — Generate report
- **Scenario 073** — Enterprise — Get report
- **Scenario 074** — Enterprise — List reports
- **Scenario 075** — Enterprise — Fraud screen (alert)
- **Scenario 076** — Enterprise — Fraud screen (no alert)
- **Scenario 077** — Enterprise — List fraud alerts
- **Scenario 078** — Enterprise — Review fraud alert
- **Scenario 079** — Enterprise — Create AML case
- **Scenario 080** — Enterprise — Get AML case
- **Scenario 081** — Enterprise — List AML cases
- **Scenario 082** — Enterprise — Submit AML screening
- **Scenario 083** — Enterprise — List admin settings
- **Scenario 084** — Enterprise — Get admin setting
- **Scenario 085** — Enterprise — Update admin setting
- **Scenario 086** — Enterprise — Admin as customer (negative)
- **Scenario 087** — Enterprise — List audit events
- **Scenario 088** — Enterprise — List domain events
- **Scenario 089** — Enterprise — Submit job
- **Scenario 090** — Enterprise — Get job
- **Scenario 091** — Enterprise — List jobs
- **Scenario 092** — Enterprise — List scheduler tasks
- **Scenario 093** — Enterprise — Register scheduler task
- **Scenario 094** — Enterprise — Register webhook
- **Scenario 095** — Enterprise — List webhooks
- **Scenario 096** — Enterprise — List webhook deliveries
- **Scenario 097** — Enterprise — List circuit breakers
- **Scenario 098** — Enterprise — Get circuit breaker
- **Scenario 099** — Lab — Get health
- **Scenario 100** — Lab — Get version
- **Scenario 101** — Lab — Reset all data
- **Scenario 102** — Lab — List scenarios
- **Scenario 103** — Lab — Get scenario payment-happy-path
- **Scenario 104** — Lab — Start scenario run
- **Scenario 105** — Lab — Complete scenario run
- **Scenario 106** — Lab — List contracts
- **Scenario 107** — Lab — Get contract file
- **Scenario 108** — Lab — List config
- **Scenario 109** — Lab — Get config key
- **Scenario 110** — Lab — Update config
- **Scenario 111** — Lab — List fault rules
- **Scenario 112** — Lab — Create fault rule
- **Scenario 113** — Lab — Disable fault rule
- **Scenario 114** — Lab — List mocks
- **Scenario 115** — Lab — Create mock
- **Scenario 116** — Lab — Call AML clear mock (public)
- **Scenario 117** — Lab — Call AML review mock (public)
- **Scenario 118** — Lab — Generate test data
- **Scenario 119** — Lab — List test data handles
- **Scenario 120** — Lab — Delete test data namespace
- **Scenario 121** — Lab — Generate seed
- **Scenario 122** — Lab — Get dashboard
- **Scenario 123** — Lab — List performance profiles
- **Scenario 124** — Lab — List security test cases
- **Scenario 125** — Lab — List concurrency scenarios
- **Scenario 126** — Lab — Get race profile
- **Scenario 127** — Lab — List failure simulations
- **Scenario 128** — Lab — Get failure simulation
- **Scenario 129** — Lab — Get scenario idempotency-replay
- **Scenario 130** — Lab — Get scenario fraud-velocity-alert
- **Scenario 131** — Lab — Get scenario webhook-retry-dlq
- **Scenario 132** — Lab — Get scenario concurrent-transfer-race
- **Scenario 133** — Lab — Get scenario bola-party-access
- **Scenario 134** — Banking — Login invalid password
- **Scenario 135** — Banking — Wrong tenant header
- **Scenario 136** — Banking — Idempotency conflict
- **Scenario 137** — Banking — Insufficient funds
- **Scenario 138** — Lab — Scenarios without auth

---

## Scenario 001 — Platform — Get health

**URL:** `http://localhost:8080/health`  
**Method:** `GET`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Accept | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "status": "UP",
  "service": "platform-api",
  "components": { "database": "UP" }
}
```

---

## Scenario 002 — Platform — Get version

**URL:** `http://localhost:8080/version`  
**Method:** `GET`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Accept | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "name": "platform-api",
  "version": "0.1.0-SNAPSHOT"
}
```

---

## Scenario 003 — Platform — Demo bad request error

**URL:** `http://localhost:8080/v1/demo/errors/bad-request`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `400 Bad Request`

```json
{
  "type": "https://playground.example/problems/bad-request",
  "title": "Bad Request",
  "status": 400,
  "detail": "Demonstration bad request",
  "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc"
}
```

---

## Scenario 004 — Platform — Demo not found error

**URL:** `http://localhost:8080/v1/demo/errors/not-found`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `404 Not Found`

```json
{
  "type": "https://playground.example/problems/not-found",
  "title": "Not Found",
  "status": 404,
  "detail": "Demonstration not found",
  "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc"
}
```

---

## Scenario 005 — Platform — Demo internal error

**URL:** `http://localhost:8080/v1/demo/errors/internal`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `500 Internal Server Error`

```json
{
  "type": "https://playground.example/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc"
}
```

---

## Scenario 006 — Platform — Validate body (valid)

**URL:** `http://localhost:8080/v1/demo/validate`  
**Method:** `POST`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "name": "Jane"
}
```

### Expected Response

**Status Code:** `200 OK`

```json
{
  "message": "Validation passed",
  "name": "Jane"
}
```

---

## Scenario 007 — Platform — Validate body (invalid)

**URL:** `http://localhost:8080/v1/demo/validate`  
**Method:** `POST`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "name": ""
}
```

### Expected Response

**Status Code:** `422 Unprocessable Entity`

```json
{
  "title": "Validation Failed",
  "status": 422,
  "errors": [{ "field": "name", "message": "name must not be blank" }],
  "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc"
}
```

---

## Scenario 008 — Banking — Login as customer

**URL:** `http://localhost:8081/v1/auth/login`  
**Method:** `POST`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Content-Type | `application/json` |

### Request Body

```json
{
  "tenantId": "tenant-demo",
  "username": "customer",
  "password": "password"
}
```

### Expected Response

**Status Code:** `200 OK`

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLWN1c3RvbWVyIiwidGVuYW50SWQiOiJ0ZW5hbnQtZGVtbyJ9.SIGNATURE",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

**Notes:** Save accessToken as jwt_token in Postman.

---

## Scenario 009 — Banking — Login as ops

**URL:** `http://localhost:8081/v1/auth/login`  
**Method:** `POST`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Content-Type | `application/json` |

### Request Body

```json
{
  "tenantId": "tenant-demo",
  "username": "ops",
  "password": "password"
}
```

### Expected Response

**Status Code:** `200 OK`

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLWN1c3RvbWVyIiwidGVuYW50SWQiOiJ0ZW5hbnQtZGVtbyJ9.SIGNATURE",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

## Scenario 010 — Banking — Login as admin

**URL:** `http://localhost:8081/v1/auth/login`  
**Method:** `POST`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Content-Type | `application/json` |

### Request Body

```json
{
  "tenantId": "tenant-demo",
  "username": "admin",
  "password": "password"
}
```

### Expected Response

**Status Code:** `200 OK`

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLWN1c3RvbWVyIiwidGVuYW50SWQiOiJ0ZW5hbnQtZGVtbyJ9.SIGNATURE",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

## Scenario 011 — Banking — Get current user

**URL:** `http://localhost:8081/v1/auth/me`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "id": "user-customer",
  "tenantId": "tenant-demo",
  "username": "customer",
  "partyId": "party-customer-1",
  "enabled": true,
  "roles": ["RETAIL_CUSTOMER"]
}
```

---

## Scenario 012 — Banking — Get health

**URL:** `http://localhost:8081/health`  
**Method:** `GET`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Accept | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "status": "UP",
  "service": "banking-api",
  "components": { "database": "UP" }
}
```

---

## Scenario 013 — Banking — Get version

**URL:** `http://localhost:8081/version`  
**Method:** `GET`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Accept | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "name": "banking-api",
  "version": "0.1.0-SNAPSHOT"
}
```

---

## Scenario 014 — Banking — List roles

**URL:** `http://localhost:8081/v1/roles`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[
  { "id": "role-customer", "tenantId": "tenant-demo", "name": "RETAIL_CUSTOMER" },
  { "id": "role-ops", "tenantId": "tenant-demo", "name": "OPS_AGENT" },
  { "id": "role-admin", "tenantId": "tenant-demo", "name": "ADMIN" }
]
```

---

## Scenario 015 — Banking — List users

**URL:** `http://localhost:8081/v1/users?page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{ "id": "user-customer", "username": "customer", "partyId": "party-customer-1", "enabled": true, "roles": ["RETAIL_CUSTOMER"] }],
  "page": 0, "size": 20, "totalElements": 3, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 016 — Banking — Get user by id

**URL:** `http://localhost:8081/v1/users/user-customer`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "id": "user-customer",
  "tenantId": "tenant-demo",
  "username": "customer",
  "partyId": "party-customer-1",
  "enabled": true,
  "roles": ["RETAIL_CUSTOMER"]
}
```

---

## Scenario 017 — Banking — Create user

**URL:** `http://localhost:8081/v1/users`  
**Method:** `POST`  
**Authentication:** Bearer JWT (admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "username": "newuser",
  "password": "password123",
  "partyId": "party-customer-1",
  "roleNames": ["RETAIL_CUSTOMER"]
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "tenantId": "tenant-demo",
  "username": "newuser",
  "partyId": "party-customer-1",
  "enabled": true,
  "roles": ["RETAIL_CUSTOMER"]
}
```

---

## Scenario 018 — Banking — Get party

**URL:** `http://localhost:8081/v1/parties/party-customer-1`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "id": "party-customer-1",
  "tenantId": "tenant-demo",
  "partyType": "INDIVIDUAL",
  "status": "ACTIVE",
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "version": 0
}
```

---

## Scenario 019 — Banking — List parties

**URL:** `http://localhost:8081/v1/parties?page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{ "id": "party-customer-1", "firstName": "Jane", "lastName": "Doe", "status": "ACTIVE" }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 020 — Banking — Create party

**URL:** `http://localhost:8081/v1/parties`  
**Method:** `POST`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "partyType": "INDIVIDUAL",
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "tenantId": "tenant-demo",
  "partyType": "INDIVIDUAL",
  "status": "PROSPECT",
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com"
}
```

---

## Scenario 021 — Banking — Update party

**URL:** `http://localhost:8081/v1/parties/party-customer-1`  
**Method:** `PATCH`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "firstName": "Jane",
  "email": "jane.updated@example.com"
}
```

### Expected Response

**Status Code:** `200 OK`

```json
{
  "id": "party-customer-1",
  "firstName": "Jane",
  "email": "jane.updated@example.com",
  "status": "ACTIVE"
}
```

---

## Scenario 022 — Banking — BOLA get other party (negative)

**URL:** `http://localhost:8081/v1/parties/party-other`  
**Method:** `GET`  
**Authentication:** Bearer JWT (customer)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `403 Forbidden`

```json
{
  "title": "Forbidden",
  "status": 403,
  "detail": "Access to this party is not permitted",
  "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc"
}
```

---

## Scenario 023 — Banking — List KYC cases

**URL:** `http://localhost:8081/v1/kyc-cases?partyId=party-customer-1`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[{ "id": "kyc-1", "partyId": "party-customer-1", "status": "APPROVED", "level": "STANDARD" }]
```

---

## Scenario 024 — Banking — Get KYC case

**URL:** `http://localhost:8081/v1/kyc-cases/kyc-1`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "kyc-1", "partyId": "party-customer-1", "status": "APPROVED", "level": "STANDARD" }
```

---

## Scenario 025 — Banking — Submit KYC case

**URL:** `http://localhost:8081/v1/kyc-cases`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{ "partyId": "party-customer-1" }
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "status": "OPEN", "level": "STANDARD" }
```

**Notes:** Fails 409 if open case already exists for party.

---

## Scenario 026 — Banking — Review KYC case

**URL:** `http://localhost:8081/v1/kyc-cases/kyc-1/review`  
**Method:** `POST`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "status": "APPROVED",
  "decisionReason": "Documents verified"
}
```

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "kyc-1", "status": "APPROVED", "decisionReason": "Documents verified" }
```

---

## Scenario 027 — Banking — List accounts

**URL:** `http://localhost:8081/v1/accounts?partyId=party-customer-1&page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [
    { "id": "acct-customer-1", "accountNumber": "GB1234567890", "currency": "GBP", "productCode": "CURRENT", "status": "ACTIVE", "availableBalance": 5000.00, "ledgerBalance": 5000.00 },
    { "id": "acct-customer-2", "accountNumber": "GB9876543210", "currency": "GBP", "productCode": "SAVINGS", "status": "ACTIVE", "availableBalance": 1000.00, "ledgerBalance": 1000.00 }
  ],
  "page": 0, "size": 20, "totalElements": 3, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 028 — Banking — Get account

**URL:** `http://localhost:8081/v1/accounts/acct-customer-1`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "id": "acct-customer-1",
  "tenantId": "tenant-demo",
  "partyId": "party-customer-1",
  "accountNumber": "GB1234567890",
  "currency": "GBP",
  "productCode": "CURRENT",
  "status": "ACTIVE",
  "availableBalance": 5000.00,
  "ledgerBalance": 5000.00,
  "version": 0
}
```

---

## Scenario 029 — Banking — Open account

**URL:** `http://localhost:8081/v1/accounts`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "partyId": "party-customer-1",
  "currency": "GBP",
  "productCode": "CURRENT"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "partyId": "party-customer-1",
  "currency": "GBP",
  "productCode": "CURRENT",
  "status": "ACTIVE",
  "availableBalance": 0.00,
  "ledgerBalance": 0.00
}
```

---

## Scenario 030 — Banking — List beneficiaries

**URL:** `http://localhost:8081/v1/beneficiaries?partyId=party-customer-1`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[{ "id": "ben-1", "partyId": "party-customer-1", "nickname": "Savings Pot", "status": "ACTIVE", "sortCode": "12-34-56", "accountNumber": "87654321" }]
```

---

## Scenario 031 — Banking — Add beneficiary

**URL:** `http://localhost:8081/v1/beneficiaries`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "partyId": "party-customer-1",
  "nickname": "Landlord",
  "sortCode": "11-22-33",
  "accountNumber": "12345678"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "nickname": "Landlord", "status": "PENDING_VERIFICATION" }
```

---

## Scenario 032 — Banking — Delete beneficiary

**URL:** `http://localhost:8081/v1/beneficiaries/ben-1`  
**Method:** `DELETE`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `204 No Content`

**Response Body:** None (empty)

---

## Scenario 033 — Banking — Create payment

**URL:** `http://localhost:8081/v1/payments`  
**Method:** `POST`  
**Authentication:** Bearer JWT + Idempotency-Key

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |
| Idempotency-Key | `{{idempotency_key}}` |

### Request Body

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

### Expected Response

**Status Code:** `201 Created`

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "transactionId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "partyId": "party-customer-1",
  "accountId": "acct-customer-1",
  "beneficiaryId": "ben-1",
  "amount": 25.50,
  "currency": "GBP",
  "reference": "rent",
  "status": "COMPLETED",
  "createdAt": "2026-07-08T12:00:00Z"
}
```

---

## Scenario 034 — Banking — Get payment

**URL:** `http://localhost:8081/v1/payments/a1b2c3d4-e5f6-7890-abcd-ef1234567890`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "transactionId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "partyId": "party-customer-1",
  "accountId": "acct-customer-1",
  "beneficiaryId": "ben-1",
  "amount": 25.50,
  "currency": "GBP",
  "reference": "rent",
  "status": "COMPLETED",
  "createdAt": "2026-07-08T12:00:00Z"
}
```

---

## Scenario 035 — Banking — List payments

**URL:** `http://localhost:8081/v1/payments?partyId=party-customer-1&page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "transactionId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
    "partyId": "party-customer-1",
    "accountId": "acct-customer-1",
    "beneficiaryId": "ben-1",
    "amount": 25.50,
    "currency": "GBP",
    "reference": "rent",
    "status": "COMPLETED",
    "createdAt": "2026-07-08T12:00:00Z"
  }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 036 — Banking — Payment without auth (negative)

**URL:** `http://localhost:8081/v1/payments`  
**Method:** `POST`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Content-Type | `application/json` |
| Idempotency-Key | `{{idempotency_key}}` |

### Request Body

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

### Expected Response

**Status Code:** `401 Unauthorized`

```json
{
  "title": "Unauthorized",
  "status": 401,
  "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc"
}
```

---

## Scenario 037 — Banking — Create transfer

**URL:** `http://localhost:8081/v1/transfers`  
**Method:** `POST`  
**Authentication:** Bearer JWT + Idempotency-Key

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |
| Idempotency-Key | `{{idempotency_key}}` |

### Request Body

```json
{
  "fromAccountId": "acct-customer-1",
  "toAccountId": "acct-customer-2",
  "amount": 50.00,
  "currency": "GBP",
  "reference": "savings"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "transactionId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "fromAccountId": "acct-customer-1",
  "toAccountId": "acct-customer-2",
  "amount": 50.00,
  "currency": "GBP",
  "reference": "savings",
  "status": "COMPLETED"
}
```

---

## Scenario 038 — Banking — Get transfer

**URL:** `http://localhost:8081/v1/transfers/a1b2c3d4-e5f6-7890-abcd-ef1234567890`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "transactionId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "fromAccountId": "acct-customer-1",
  "toAccountId": "acct-customer-2",
  "amount": 50.00,
  "currency": "GBP",
  "reference": "savings",
  "status": "COMPLETED"
}
```

---

## Scenario 039 — Banking — List transfers

**URL:** `http://localhost:8081/v1/transfers?accountId=acct-customer-1&page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "transactionId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
    "fromAccountId": "acct-customer-1",
    "toAccountId": "acct-customer-2",
    "amount": 50.00,
    "currency": "GBP",
    "reference": "savings",
    "status": "COMPLETED"
  }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 040 — Banking — Issue card

**URL:** `http://localhost:8081/v1/cards`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "partyId": "party-customer-1",
  "accountId": "acct-customer-1",
  "productCode": "DEBIT"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "accountId": "acct-customer-1", "panLast4": "1234", "productCode": "DEBIT", "status": "ACTIVE" }
```

---

## Scenario 041 — Banking — List cards

**URL:** `http://localhost:8081/v1/cards?partyId=party-customer-1`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[{ "id": "card-1", "partyId": "party-customer-1", "accountId": "acct-customer-1", "panLast4": "4242", "productCode": "DEBIT", "status": "ACTIVE" }]
```

---

## Scenario 042 — Banking — Get card

**URL:** `http://localhost:8081/v1/cards/card-1`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "card-1", "panLast4": "4242", "status": "ACTIVE" }
```

---

## Scenario 043 — Banking — Authorize card

**URL:** `http://localhost:8081/v1/cards/card-1/authorizations`  
**Method:** `POST`  
**Authentication:** Bearer JWT + Idempotency-Key

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |
| Idempotency-Key | `{{idempotency_key}}` |

### Request Body

```json
{
  "merchantName": "Coffee Shop",
  "amount": 4.50,
  "currency": "GBP"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "cardId": "card-1", "merchantName": "Coffee Shop", "amount": 4.50, "currency": "GBP", "status": "APPROVED" }
```

---

## Scenario 044 — Banking — Create FX quote

**URL:** `http://localhost:8081/v1/fx/quotes`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "fromCurrency": "GBP",
  "toCurrency": "EUR",
  "fromAmount": 100.00
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "c3d4e5f6-a7b8-9012-cdef-345678901234", "fromCurrency": "GBP", "toCurrency": "EUR", "rate": 1.17, "fromAmount": 100.00, "toAmount": 117.00, "expiresAt": "2026-07-08T12:05:00Z" }
```

---

## Scenario 045 — Banking — Execute FX conversion

**URL:** `http://localhost:8081/v1/fx/conversions`  
**Method:** `POST`  
**Authentication:** Bearer JWT + Idempotency-Key

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |
| Idempotency-Key | `{{idempotency_key}}` |

### Request Body

```json
{
  "quoteId": "c3d4e5f6-a7b8-9012-cdef-345678901234",
  "fromAccountId": "acct-customer-1",
  "toAccountId": "acct-customer-eur",
  "partyId": "party-customer-1"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "quoteId": "c3d4e5f6-a7b8-9012-cdef-345678901234", "fromAmount": 100.00, "toAmount": 117.00, "fromCurrency": "GBP", "toCurrency": "EUR", "rate": 1.17, "status": "COMPLETED" }
```

---

## Scenario 046 — Banking — Get FX conversion

**URL:** `http://localhost:8081/v1/fx/conversions/a1b2c3d4-e5f6-7890-abcd-ef1234567890`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "fromCurrency": "GBP", "toCurrency": "EUR", "rate": 1.17, "fromAmount": 100.00, "toAmount": 117.00, "status": "COMPLETED" }
```

---

## Scenario 047 — Banking — List transactions

**URL:** `http://localhost:8081/v1/transactions?accountId=acct-customer-1&page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "txnType": "PAYMENT", "status": "COMPLETED", "amount": 25.50, "currency": "GBP" }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 048 — Banking — Get transaction

**URL:** `http://localhost:8081/v1/transactions/b2c3d4e5-f6a7-8901-bcde-f12345678901`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901", "txnType": "PAYMENT", "status": "COMPLETED", "amount": 25.50, "currency": "GBP", "accountId": "acct-customer-1" }
```

---

## Scenario 049 — Banking — List ledger entries

**URL:** `http://localhost:8081/v1/ledger-entries?accountId=acct-customer-1&page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "entryType": "DEBIT", "amount": 25.50, "currency": "GBP", "balanceAfter": 4974.50 }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 050 — Banking — Generate statement

**URL:** `http://localhost:8081/v1/accounts/acct-customer-1/statements`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "periodStart": "2026-01-01",
  "periodEnd": "2026-01-31"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "accountId": "acct-customer-1", "periodStart": "2026-01-01", "periodEnd": "2026-01-31", "openingBalance": 5000.00, "closingBalance": 4974.50, "currency": "GBP", "status": "PUBLISHED" }
```

---

## Scenario 051 — Banking — List statements

**URL:** `http://localhost:8081/v1/accounts/acct-customer-1/statements?page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "periodStart": "2026-01-01", "periodEnd": "2026-01-31", "status": "PUBLISHED" }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 052 — Banking — Get statement

**URL:** `http://localhost:8081/v1/statements/a1b2c3d4-e5f6-7890-abcd-ef1234567890`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "accountId": "acct-customer-1", "periodStart": "2026-01-01", "periodEnd": "2026-01-31", "openingBalance": 5000.00, "closingBalance": 4974.50, "currency": "GBP", "lines": [{ "description": "Payment rent", "amount": -25.50, "postedAt": "2026-07-08T12:00:00Z" }] }
```

---

## Scenario 053 — Enterprise — Get health

**URL:** `http://localhost:8082/health`  
**Method:** `GET`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Accept | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "status": "UP",
  "service": "enterprise-api",
  "components": { "database": "UP" }
}
```

---

## Scenario 054 — Enterprise — Get version

**URL:** `http://localhost:8082/version`  
**Method:** `GET`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Accept | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "name": "enterprise-api",
  "version": "0.1.0-SNAPSHOT"
}
```

---

## Scenario 055 — Enterprise — Create loan

**URL:** `http://localhost:8082/v1/loans`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

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

### Expected Response

**Status Code:** `201 Created`

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "partyId": "party-customer-1",
  "accountId": "acct-customer-1",
  "productCode": "PERSONAL",
  "principal": 10000.00,
  "currency": "GBP",
  "interestRate": 5.5,
  "termMonths": 24,
  "status": "ACTIVE",
  "outstandingBalance": 10000.00
}
```

---

## Scenario 056 — Enterprise — Get loan

**URL:** `http://localhost:8082/v1/loans/a1b2c3d4-e5f6-7890-abcd-ef1234567890`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "partyId": "party-customer-1",
  "accountId": "acct-customer-1",
  "productCode": "PERSONAL",
  "principal": 10000.00,
  "currency": "GBP",
  "interestRate": 5.5,
  "termMonths": 24,
  "status": "ACTIVE",
  "outstandingBalance": 10000.00
}
```

---

## Scenario 057 — Enterprise — List loans

**URL:** `http://localhost:8082/v1/loans?partyId=party-customer-1&page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "partyId": "party-customer-1",
    "accountId": "acct-customer-1",
    "productCode": "PERSONAL",
    "principal": 10000.00,
    "currency": "GBP",
    "interestRate": 5.5,
    "termMonths": 24,
    "status": "ACTIVE",
    "outstandingBalance": 10000.00
  }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 058 — Enterprise — Create loan repayment

**URL:** `http://localhost:8082/v1/loans/a1b2c3d4-e5f6-7890-abcd-ef1234567890/repayments`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{ "amount": 500.00 }
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901", "loanId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "amount": 500.00, "currency": "GBP", "status": "COMPLETED" }
```

---

## Scenario 059 — Enterprise — Create fixed deposit

**URL:** `http://localhost:8082/v1/fixed-deposits`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

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

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "principal": 5000.00, "currency": "GBP", "termDays": 365, "status": "ACTIVE", "maturityDate": "2027-07-08" }
```

---

## Scenario 060 — Enterprise — Get fixed deposit

**URL:** `http://localhost:8082/v1/fixed-deposits/a1b2c3d4-e5f6-7890-abcd-ef1234567890`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "principal": 5000.00, "currency": "GBP", "termDays": 365, "status": "ACTIVE", "maturityDate": "2027-07-08" }
```

---

## Scenario 061 — Enterprise — List fixed deposits

**URL:** `http://localhost:8082/v1/fixed-deposits?partyId=party-customer-1&page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "partyId": "party-customer-1",
    "principal": 5000.00,
    "currency": "GBP",
    "termDays": 365,
    "status": "ACTIVE",
    "maturityDate": "2027-07-08"
  }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 062 — Enterprise — Create recurring deposit

**URL:** `http://localhost:8082/v1/recurring-deposits`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "partyId": "party-customer-1",
  "accountId": "acct-customer-1",
  "installmentAmount": 200.00,
  "currency": "GBP",
  "frequency": "MONTHLY"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "installmentAmount": 200.00, "currency": "GBP", "frequency": "MONTHLY", "status": "ACTIVE" }
```

---

## Scenario 063 — Enterprise — Get recurring deposit

**URL:** `http://localhost:8082/v1/recurring-deposits/a1b2c3d4-e5f6-7890-abcd-ef1234567890`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "installmentAmount": 200.00, "currency": "GBP", "frequency": "MONTHLY", "status": "ACTIVE" }
```

---

## Scenario 064 — Enterprise — List recurring deposits

**URL:** `http://localhost:8082/v1/recurring-deposits?partyId=party-customer-1&page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "partyId": "party-customer-1",
    "installmentAmount": 200.00,
    "currency": "GBP",
    "frequency": "MONTHLY",
    "status": "ACTIVE"
  }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 065 — Enterprise — Post RD installment

**URL:** `http://localhost:8082/v1/recurring-deposits/a1b2c3d4-e5f6-7890-abcd-ef1234567890/installments`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901", "recurringDepositId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "amount": 200.00, "currency": "GBP", "status": "PAID" }
```

---

## Scenario 066 — Enterprise — Create notification

**URL:** `http://localhost:8082/v1/notifications`  
**Method:** `POST`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "partyId": "party-customer-1",
  "channel": "EMAIL",
  "subject": "Loan approved",
  "body": "Your loan has been approved."
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "channel": "EMAIL", "subject": "Loan approved", "status": "UNREAD" }
```

---

## Scenario 067 — Enterprise — List notifications

**URL:** `http://localhost:8082/v1/notifications?partyId=party-customer-1&page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "partyId": "party-customer-1",
    "channel": "EMAIL",
    "subject": "Loan approved",
    "status": "UNREAD"
  }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 068 — Enterprise — Mark notification read

**URL:** `http://localhost:8082/v1/notifications/a1b2c3d4-e5f6-7890-abcd-ef1234567890/read`  
**Method:** `PATCH`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "channel": "EMAIL", "subject": "Loan approved", "status": "READ", "readAt": "2026-07-08T12:00:00Z" }
```

---

## Scenario 069 — Enterprise — Register document

**URL:** `http://localhost:8082/v1/documents`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "partyId": "party-customer-1",
  "documentType": "PASSPORT",
  "fileName": "passport.pdf",
  "contentType": "application/pdf"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "documentType": "PASSPORT", "fileName": "passport.pdf", "status": "UPLOADED" }
```

---

## Scenario 070 — Enterprise — Get document

**URL:** `http://localhost:8082/v1/documents/a1b2c3d4-e5f6-7890-abcd-ef1234567890`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "documentType": "PASSPORT", "fileName": "passport.pdf", "contentType": "application/pdf", "status": "UPLOADED" }
```

---

## Scenario 071 — Enterprise — List documents

**URL:** `http://localhost:8082/v1/documents?partyId=party-customer-1&page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "partyId": "party-customer-1",
    "documentType": "PASSPORT",
    "fileName": "passport.pdf",
    "status": "UPLOADED"
  }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 072 — Enterprise — Generate report

**URL:** `http://localhost:8082/v1/reports`  
**Method:** `POST`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "reportType": "LOAN_PORTFOLIO",
  "parameters": "partyId=party-customer-1"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "reportType": "LOAN_PORTFOLIO", "status": "PENDING" }
```

---

## Scenario 073 — Enterprise — Get report

**URL:** `http://localhost:8082/v1/reports/a1b2c3d4-e5f6-7890-abcd-ef1234567890`  
**Method:** `GET`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "reportType": "LOAN_PORTFOLIO", "status": "COMPLETED", "resultLocation": "/reports/a1b2c3d4-e5f6-7890-abcd-ef1234567890.csv", "generatedAt": "2026-07-08T12:00:00Z" }
```

---

## Scenario 074 — Enterprise — List reports

**URL:** `http://localhost:8082/v1/reports?page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "reportType": "LOAN_PORTFOLIO",
    "status": "COMPLETED",
    "resultLocation": "/reports/a1b2c3d4-e5f6-7890-abcd-ef1234567890.csv"
  }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 075 — Enterprise — Fraud screen (alert)

**URL:** `http://localhost:8082/v1/fraud/screen`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "partyId": "party-customer-1",
  "entityType": "PAYMENT",
  "entityId": "pay-lab-1",
  "amount": 6000.00
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "partyId": "party-customer-1",
  "entityType": "PAYMENT",
  "entityId": "pay-lab-1",
  "ruleCode": "VELOCITY",
  "riskScore": 85,
  "status": "OPEN",
  "details": "Amount exceeds velocity threshold"
}
```

---

## Scenario 076 — Enterprise — Fraud screen (no alert)

**URL:** `http://localhost:8082/v1/fraud/screen`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "partyId": "party-customer-1",
  "entityType": "PAYMENT",
  "entityId": "pay-lab-2",
  "amount": 100.00
}
```

### Expected Response

**Status Code:** `200 OK`

**Response Body:** None (empty)

**Notes:** Amount 100.00 is below fraud.velocity.threshold (5000). Response has no JSON body.

---

## Scenario 077 — Enterprise — List fraud alerts

**URL:** `http://localhost:8082/v1/fraud/alerts?partyId=party-customer-1&page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "partyId": "party-customer-1",
    "entityType": "PAYMENT",
    "entityId": "pay-lab-1",
    "ruleCode": "VELOCITY",
    "riskScore": 85,
    "status": "OPEN",
    "details": "Amount exceeds velocity threshold"
  }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 078 — Enterprise — Review fraud alert

**URL:** `http://localhost:8082/v1/fraud/alerts/a1b2c3d4-e5f6-7890-abcd-ef1234567890/review`  
**Method:** `PATCH`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{ "status": "REVIEWED" }
```

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "status": "REVIEWED", "reviewedAt": "2026-07-08T12:00:00Z" }
```

---

## Scenario 079 — Enterprise — Create AML case

**URL:** `http://localhost:8082/v1/aml/cases`  
**Method:** `POST`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "partyId": "party-customer-1",
  "caseType": "SANCTIONS",
  "priority": "HIGH"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "caseType": "SANCTIONS", "status": "OPEN", "priority": "HIGH" }
```

---

## Scenario 080 — Enterprise — Get AML case

**URL:** `http://localhost:8082/v1/aml/cases/a1b2c3d4-e5f6-7890-abcd-ef1234567890`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "caseType": "SANCTIONS", "status": "OPEN", "priority": "HIGH" }
```

---

## Scenario 081 — Enterprise — List AML cases

**URL:** `http://localhost:8082/v1/aml/cases?partyId=party-customer-1&page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "partyId": "party-customer-1",
    "caseType": "SANCTIONS",
    "status": "OPEN",
    "priority": "HIGH"
  }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 082 — Enterprise — Submit AML screening

**URL:** `http://localhost:8082/v1/aml/screenings`  
**Method:** `POST`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "partyId": "party-customer-1",
  "screeningType": "SANCTIONS"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "screeningType": "SANCTIONS", "result": "CLEAR", "matchScore": 0 }
```

---

## Scenario 083 — Enterprise — List admin settings

**URL:** `http://localhost:8082/v1/admin/settings`  
**Method:** `GET`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[{ "settingKey": "fraud.velocity.threshold", "settingValue": "5000", "updatedBy": "user-admin" }, { "settingKey": "aml.auto_escalate_score", "settingValue": "80", "updatedBy": "user-admin" }]
```

---

## Scenario 084 — Enterprise — Get admin setting

**URL:** `http://localhost:8082/v1/admin/settings/fraud.velocity.threshold`  
**Method:** `GET`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "settingKey": "fraud.velocity.threshold", "settingValue": "5000", "updatedBy": "user-admin" }
```

---

## Scenario 085 — Enterprise — Update admin setting

**URL:** `http://localhost:8082/v1/admin/settings/fraud.velocity.threshold`  
**Method:** `PUT`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{ "settingValue": "6000" }
```

### Expected Response

**Status Code:** `200 OK`

```json
{ "settingKey": "fraud.velocity.threshold", "settingValue": "6000" }
```

---

## Scenario 086 — Enterprise — Admin as customer (negative)

**URL:** `http://localhost:8082/v1/admin/settings`  
**Method:** `GET`  
**Authentication:** Bearer JWT (customer)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `403 Forbidden`

```json
{ "title": "Forbidden", "status": 403, "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc" }
```

---

## Scenario 087 — Enterprise — List audit events

**URL:** `http://localhost:8082/v1/audit/events?page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "entityType": "PAYMENT", "action": "CREATE", "actorId": "user-customer", "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc" }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 088 — Enterprise — List domain events

**URL:** `http://localhost:8082/v1/events?page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "eventType": "FRAUD_ALERT_CREATED", "status": "PUBLISHED" }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 089 — Enterprise — Submit job

**URL:** `http://localhost:8082/v1/jobs`  
**Method:** `POST`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "jobType": "REPORT_GENERATION",
  "payload": "reportId=abc-123"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "jobType": "REPORT_GENERATION", "status": "PENDING", "progress": 0 }
```

---

## Scenario 090 — Enterprise — Get job

**URL:** `http://localhost:8082/v1/jobs/a1b2c3d4-e5f6-7890-abcd-ef1234567890`  
**Method:** `GET`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "jobType": "REPORT_GENERATION", "status": "COMPLETED", "progress": 100, "completedAt": "2026-07-08T12:01:00Z" }
```

---

## Scenario 091 — Enterprise — List jobs

**URL:** `http://localhost:8082/v1/jobs?page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "jobType": "REPORT_GENERATION",
    "status": "COMPLETED",
    "progress": 100
  }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 092 — Enterprise — List scheduler tasks

**URL:** `http://localhost:8082/v1/scheduler/tasks?page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{ "id": "sched-rd-reminder", "taskType": "RD_INSTALLMENT_REMINDER", "cronExpression": "0 0 9 * * *", "enabled": true }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 093 — Enterprise — Register scheduler task

**URL:** `http://localhost:8082/v1/scheduler/tasks`  
**Method:** `POST`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "taskType": "RD_INSTALLMENT_REMINDER",
  "cronExpression": "0 0 9 * * *",
  "payload": "{\"partyId\":\"party-customer-1\"}"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "taskType": "RD_INSTALLMENT_REMINDER", "enabled": true }
```

---

## Scenario 094 — Enterprise — Register webhook

**URL:** `http://localhost:8082/v1/webhooks`  
**Method:** `POST`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "eventType": "FRAUD_ALERT_CREATED",
  "targetUrl": "http://localhost:8083/v1/mocks/webhooks/fail",
  "secret": "my-hmac-secret"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "eventType": "FRAUD_ALERT_CREATED", "targetUrl": "http://localhost:8083/v1/mocks/webhooks/fail", "status": "ACTIVE" }
```

---

## Scenario 095 — Enterprise — List webhooks

**URL:** `http://localhost:8082/v1/webhooks?page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "eventType": "FRAUD_ALERT_CREATED",
    "targetUrl": "http://localhost:8083/v1/mocks/webhooks/fail",
    "status": "ACTIVE"
  }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 096 — Enterprise — List webhook deliveries

**URL:** `http://localhost:8082/v1/webhooks/a1b2c3d4-e5f6-7890-abcd-ef1234567890/deliveries?page=0&size=20`  
**Method:** `GET`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "items": [{ "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901", "webhookId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "status": "DLQ", "attemptCount": 3, "httpStatus": 500, "lastAttemptAt": "2026-07-08T12:00:00Z" }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null
}
```

---

## Scenario 097 — Enterprise — List circuit breakers

**URL:** `http://localhost:8082/v1/resilience/circuit-breakers`  
**Method:** `GET`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[{ "name": "webhook-delivery", "state": "CLOSED", "failureCount": 0, "successCount": 10 }]
```

---

## Scenario 098 — Enterprise — Get circuit breaker

**URL:** `http://localhost:8082/v1/resilience/circuit-breakers/webhook-delivery`  
**Method:** `GET`  
**Authentication:** Bearer JWT (ops/admin)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{admin_jwt}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "name": "webhook-delivery", "state": "CLOSED", "failureCount": 0, "successCount": 10 }
```

---

## Scenario 099 — Lab — Get health

**URL:** `http://localhost:8083/health`  
**Method:** `GET`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Accept | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "status": "UP",
  "service": "playground-api",
  "components": { "database": "UP" }
}
```

---

## Scenario 100 — Lab — Get version

**URL:** `http://localhost:8083/version`  
**Method:** `GET`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Accept | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "name": "playground-api",
  "version": "0.1.0-SNAPSHOT"
}
```

---

## Scenario 101 — Lab — Reset all data

**URL:** `http://localhost:8083/v1/playground/reset`  
**Method:** `POST`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |
| Content-Type | `application/json` |

### Request Body

```json
{ "scope": "ALL" }
```

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "scope": "ALL", "status": "COMPLETED", "details": "Playground lab tables cleared; Banking transactional data cleared; Enterprise transactional data cleared" }
```

---

## Scenario 102 — Lab — List scenarios

**URL:** `http://localhost:8083/v1/scenarios`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[{ "slug": "payment-happy-path", "title": "Payment Happy Path", "difficulty": "L1" }, { "slug": "idempotency-replay", "title": "Idempotency Replay", "difficulty": "L2" }]
```

---

## Scenario 103 — Lab — Get scenario payment-happy-path

**URL:** `http://localhost:8083/v1/scenarios/payment-happy-path`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "slug": "payment-happy-path",
  "title": "Payment Happy Path",
  "difficulty": "L1",
  "learningObjectives": ["LO-REST-01", "LO-REST-08"],
  "tags": ["payments", "idempotency"],
  "description": "Authenticate as customer, create a payment to a seeded beneficiary, and verify ledger balance decreases. Replay with the same Idempotency-Key to observe safe retry.",
  "steps": [
    { "action": "login", "service": "banking-api", "path": "POST /v1/auth/login" },
    { "action": "create_payment", "service": "banking-api", "path": "POST /v1/payments", "headers": { "Idempotency-Key": "{{uuid}}" } },
    { "action": "verify_transaction", "service": "banking-api", "path": "GET /v1/transactions" }
  ],
  "rubric": [
    { "signal": "payment.status", "expected": "COMPLETED" },
    { "signal": "idempotency.replayed", "expected": false }
  ],
  "expectedSignals": [
    "correlationId present in response",
    "application/problem+json on validation errors"
  ]
}
```

---

## Scenario 104 — Lab — Start scenario run

**URL:** `http://localhost:8083/v1/scenarios/payment-happy-path/runs`  
**Method:** `POST`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "scenarioSlug": "payment-happy-path", "status": "RUNNING", "startedAt": "2026-07-08T12:00:00Z", "completedAt": null }
```

---

## Scenario 105 — Lab — Complete scenario run

**URL:** `http://localhost:8083/v1/scenarios/runs/a1b2c3d4-e5f6-7890-abcd-ef1234567890`  
**Method:** `PATCH`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |
| Content-Type | `application/json` |

### Request Body

```json
{ "status": "COMPLETED" }
```

### Expected Response

**Status Code:** `200 OK`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "scenarioSlug": "payment-happy-path", "status": "COMPLETED", "startedAt": "2026-07-08T12:00:00Z", "completedAt": "2026-07-08T12:05:00Z" }
```

---

## Scenario 106 — Lab — List contracts

**URL:** `http://localhost:8083/v1/contracts`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[{ "service": "banking-api", "filename": "openapi.yaml" }, { "service": "enterprise-api", "filename": "openapi.yaml" }]
```

---

## Scenario 107 — Lab — Get contract file

**URL:** `http://localhost:8083/v1/contracts/banking-api/openapi.yaml`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "service": "banking-api",
  "filename": "openapi.yaml",
  "content": "openapi: 3.1.0\ninfo:\n  title: Banking API\n  version: 0.1.0-SNAPSHOT\nservers:\n  - url: http://localhost:8081\npaths:\n  /v1/auth/login:\n    post:\n      summary: Login\n"
}
```

---

## Scenario 108 — Lab — List config

**URL:** `http://localhost:8083/v1/playground/config`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[{ "key": "default_tenant", "value": "tenant-demo" }, { "key": "reset_enabled", "value": "true" }]
```

---

## Scenario 109 — Lab — Get config key

**URL:** `http://localhost:8083/v1/playground/config/default_tenant`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "key": "default_tenant", "value": "tenant-demo" }
```

---

## Scenario 110 — Lab — Update config

**URL:** `http://localhost:8083/v1/playground/config/default_tenant`  
**Method:** `PUT`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |
| Content-Type | `application/json` |

### Request Body

```json
{ "value": "tenant-demo" }
```

### Expected Response

**Status Code:** `200 OK`

```json
{ "key": "default_tenant", "value": "tenant-demo" }
```

---

## Scenario 111 — Lab — List fault rules

**URL:** `http://localhost:8083/v1/playground/faults`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[]
```

---

## Scenario 112 — Lab — Create fault rule

**URL:** `http://localhost:8083/v1/playground/faults`  
**Method:** `POST`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "targetService": "BANKING",
  "pathPattern": "/v1/payments",
  "faultType": "LATENCY",
  "config": { "delayMs": 500 }
}
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "targetService": "BANKING", "pathPattern": "/v1/payments", "faultType": "LATENCY", "enabled": true }
```

---

## Scenario 113 — Lab — Disable fault rule

**URL:** `http://localhost:8083/v1/playground/faults/a1b2c3d4-e5f6-7890-abcd-ef1234567890`  
**Method:** `DELETE`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `204 No Content`

**Response Body:** None (empty)

---

## Scenario 114 — Lab — List mocks

**URL:** `http://localhost:8083/v1/playground/mocks`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[{ "path": "/v1/mocks/aml/screen/clear", "httpMethod": "POST", "statusCode": 200 }]
```

---

## Scenario 115 — Lab — Create mock

**URL:** `http://localhost:8083/v1/playground/mocks`  
**Method:** `POST`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |
| Content-Type | `application/json` |

### Request Body

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

### Expected Response

**Status Code:** `201 Created`

```json
{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "path": "/v1/mocks/webhooks/fail", "statusCode": 500 }
```

---

## Scenario 116 — Lab — Call AML clear mock (public)

**URL:** `http://localhost:8083/v1/mocks/aml/screen/clear`  
**Method:** `POST`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Content-Type | `application/json` |

### Request Body

```json
{}
```

### Expected Response

**Status Code:** `200 OK`

```json
{ "result": "CLEAR", "matchScore": 0 }
```

---

## Scenario 117 — Lab — Call AML review mock (public)

**URL:** `http://localhost:8083/v1/mocks/aml/screen/review`  
**Method:** `POST`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Content-Type | `application/json` |

### Request Body

```json
{}
```

### Expected Response

**Status Code:** `200 OK`

```json
{ "result": "REVIEW", "matchScore": 75 }
```

---

## Scenario 118 — Lab — Generate test data

**URL:** `http://localhost:8083/v1/playground/test-data`  
**Method:** `POST`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |
| Content-Type | `application/json` |

### Request Body

```json
{
  "namespace": "lab-session-1",
  "profile": "retail-customer"
}
```

### Expected Response

**Status Code:** `201 Created`

```json
[{ "namespace": "lab-session-1", "entityType": "PARTY", "entityId": "party-customer-1" }, { "namespace": "lab-session-1", "entityType": "ACCOUNT", "entityId": "acct-customer-1" }]
```

---

## Scenario 119 — Lab — List test data handles

**URL:** `http://localhost:8083/v1/playground/test-data?namespace=lab-session-1`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[{ "namespace": "lab-session-1", "entityType": "PARTY", "entityId": "party-customer-1" }]
```

---

## Scenario 120 — Lab — Delete test data namespace

**URL:** `http://localhost:8083/v1/playground/test-data/lab-session-1`  
**Method:** `DELETE`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `204 No Content`

**Response Body:** None (empty)

---

## Scenario 121 — Lab — Generate seed

**URL:** `http://localhost:8083/v1/playground/seed`  
**Method:** `POST`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |
| Content-Type | `application/json` |

### Request Body

```json
{ "profile": "retail-customer" }
```

### Expected Response

**Status Code:** `201 Created`

```json
{ "profile": "retail-customer", "entities": { "party": "party-customer-1", "account": "acct-customer-1" } }
```

---

## Scenario 122 — Lab — Get dashboard

**URL:** `http://localhost:8083/v1/playground/dashboard`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "serviceHealth": { "playground-api": "UP", "platform-api": "UP", "banking-api": "UP", "enterprise-api": "UP" },
  "scenarioCount": 6,
  "activeFaultCount": 0,
  "recentRuns": [],
  "configSummary": [{ "key": "default_tenant", "value": "tenant-demo" }]
}
```

---

## Scenario 123 — Lab — List performance profiles

**URL:** `http://localhost:8083/v1/playground/performance/profiles`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "profiles": [{ "name": "payment-throughput", "targetPath": "/v1/payments", "method": "POST", "suggestedVUs": 20, "durationSec": 120 }] }
```

---

## Scenario 124 — Lab — List security test cases

**URL:** `http://localhost:8083/v1/playground/security/test-cases`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[{ "id": "bola-party-read", "category": "BOLA", "expectedStatus": 403, "method": "GET", "path": "/v1/parties/party-other" }]
```

---

## Scenario 125 — Lab — List concurrency scenarios

**URL:** `http://localhost:8083/v1/playground/concurrency/scenarios`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[{ "slug": "concurrent-transfer-race", "title": "Concurrent Transfer Race" }]
```

---

## Scenario 126 — Lab — Get race profile

**URL:** `http://localhost:8083/v1/playground/concurrency/scenarios/concurrent-transfer-race/race-profile`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "slug": "concurrent-transfer-race", "service": "banking-api", "method": "POST", "path": "/v1/transfers", "concurrency": 5 }
```

---

## Scenario 127 — Lab — List failure simulations

**URL:** `http://localhost:8083/v1/playground/failures`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
[{ "slug": "banking-latency-spike", "title": "Banking API Latency Spike" }]
```

---

## Scenario 128 — Lab — Get failure simulation

**URL:** `http://localhost:8083/v1/playground/failures/banking-latency-spike`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{ "slug": "banking-latency-spike", "faultType": "LATENCY", "pathPattern": "/v1/payments/**", "config": { "delayMs": 2000 } }
```

---

## Scenario 129 — Lab — Get scenario idempotency-replay

**URL:** `http://localhost:8083/v1/scenarios/idempotency-replay`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "slug": "idempotency-replay",
  "title": "Idempotency Replay",
  "difficulty": "L2",
  "learningObjectives": ["LO-REST-08"],
  "tags": ["payments", "idempotency"],
  "description": "Submit the same payment twice with an identical Idempotency-Key and body. The second response must replay the first without double-posting.",
  "steps": [
    { "action": "login", "service": "banking-api" },
    { "action": "create_payment", "service": "banking-api", "note": "Use fixed Idempotency-Key idem-lab-1" },
    { "action": "replay_payment", "service": "banking-api", "note": "Same key and body — expect replay" }
  ],
  "rubric": [
    { "signal": "second_response.replayed", "expected": true },
    { "signal": "account.balance_delta", "expected": "single_debit_only" }
  ]
}
```

---

## Scenario 130 — Lab — Get scenario fraud-velocity-alert

**URL:** `http://localhost:8083/v1/scenarios/fraud-velocity-alert`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "slug": "fraud-velocity-alert",
  "title": "Fraud Velocity Alert",
  "difficulty": "L2",
  "learningObjectives": ["LO-ASYNC-02"],
  "tags": ["fraud", "enterprise-api"],
  "description": "Screen a high-value transaction via enterprise-api fraud endpoint and verify an OPEN alert is created when amount exceeds velocity threshold.",
  "steps": [
    { "action": "login", "service": "banking-api" },
    { "action": "fraud_screen", "service": "enterprise-api", "path": "POST /v1/fraud/screen", "body": { "amount": 6000.00 } }
  ],
  "rubric": [
    { "signal": "alert.status", "expected": "OPEN" },
    { "signal": "alert.riskScore", "expected": 85 }
  ]
}
```

---

## Scenario 131 — Lab — Get scenario webhook-retry-dlq

**URL:** `http://localhost:8083/v1/scenarios/webhook-retry-dlq`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "slug": "webhook-retry-dlq",
  "title": "Webhook Retry and DLQ",
  "difficulty": "L3",
  "learningObjectives": ["LO-ASYNC-02", "LO-RES-01"],
  "tags": ["webhooks", "resilience"],
  "description": "Register a webhook pointing to a failing mock URL, trigger an event, and observe retry attempts before DLQ status.",
  "steps": [
    { "action": "login_as_ops", "service": "banking-api" },
    { "action": "register_webhook", "service": "enterprise-api", "target_url": "http://localhost:8083/v1/mocks/webhooks/fail" },
    { "action": "trigger_event", "service": "enterprise-api" }
  ],
  "rubric": [
    { "signal": "delivery.attempt_count", "expected": ">1" },
    { "signal": "delivery.final_status", "expected": "DLQ" }
  ]
}
```

---

## Scenario 132 — Lab — Get scenario concurrent-transfer-race

**URL:** `http://localhost:8083/v1/scenarios/concurrent-transfer-race`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "slug": "concurrent-transfer-race",
  "title": "Concurrent Transfer Race",
  "difficulty": "L3",
  "learningObjectives": ["LO-REST-11"],
  "tags": ["concurrency", "transfers"],
  "description": "Fire parallel transfers from the same account and observe optimistic locking (409 Conflict) when balances race.",
  "steps": [
    { "action": "login", "service": "banking-api" },
    { "action": "parallel_transfers", "service": "banking-api", "concurrency": 5 }
  ],
  "rubric": [
    { "signal": "at_least_one.conflict", "expected": true },
    { "signal": "final_balance.consistent", "expected": true }
  ]
}
```

---

## Scenario 133 — Lab — Get scenario bola-party-access

**URL:** `http://localhost:8083/v1/scenarios/bola-party-access`  
**Method:** `GET`  
**Authentication:** Basic (learner/learner)

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Basic bGVhcm5lcjpsZWFybmVy` |

### Request Body

None

### Expected Response

**Status Code:** `200 OK`

```json
{
  "slug": "bola-party-access",
  "title": "BOLA Party Access",
  "difficulty": "L2",
  "learningObjectives": ["LO-SEC-12"],
  "tags": ["security", "authorization"],
  "description": "Verify a retail customer cannot read another party's account. Use JWT party_id mismatch and expect 403 Forbidden with RFC 7807 problem+json.",
  "steps": [
    { "action": "login_as_customer", "service": "banking-api" },
    { "action": "access_other_party", "service": "banking-api", "path": "GET /v1/parties/party-other", "expect_status": 403 }
  ],
  "rubric": [
    { "signal": "response.content_type", "expected": "application/problem+json" },
    { "signal": "response.status", "expected": 403 }
  ]
}
```

---

## Scenario 134 — Banking — Login invalid password

**URL:** `http://localhost:8081/v1/auth/login`  
**Method:** `POST`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Content-Type | `application/json` |

### Request Body

```json
{
  "tenantId": "tenant-demo",
  "username": "customer",
  "password": "wrong"
}
```

### Expected Response

**Status Code:** `401 Unauthorized`

```json
{ "title": "Unauthorized", "status": 401, "detail": "Invalid credentials", "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc" }
```

---

## Scenario 135 — Banking — Wrong tenant header

**URL:** `http://localhost:8081/v1/accounts?partyId=party-customer-1`  
**Method:** `GET`  
**Authentication:** Bearer JWT

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `wrong-tenant` |
| Content-Type | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `403 Forbidden`

```json
{ "title": "Forbidden", "status": 403, "detail": "X-Tenant-Id does not match token tenant", "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc" }
```

---

## Scenario 136 — Banking — Idempotency conflict

**URL:** `http://localhost:8081/v1/payments`  
**Method:** `POST`  
**Authentication:** Bearer JWT + Idempotency-Key

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |
| Idempotency-Key | `fixed-key-123` |

### Request Body

```json
{
  "partyId": "party-customer-1",
  "accountId": "acct-customer-1",
  "beneficiaryId": "ben-1",
  "amount": 99.00,
  "currency": "GBP",
  "reference": "conflict"
}
```

### Expected Response

**Status Code:** `409 Conflict`

```json
{ "title": "Conflict", "status": 409, "detail": "Idempotency-Key was already used with a different request body", "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc" }
```

**Notes:** Send after successful payment with same key but different amount.

---

## Scenario 137 — Banking — Insufficient funds

**URL:** `http://localhost:8081/v1/payments`  
**Method:** `POST`  
**Authentication:** Bearer JWT + Idempotency-Key

### Headers

| Header | Value |
|--------|-------|
| Authorization | `Bearer {{jwt_token}}` |
| X-Tenant-Id | `tenant-demo` |
| Content-Type | `application/json` |
| Idempotency-Key | `{{idempotency_key}}` |

### Request Body

```json
{
  "partyId": "party-customer-1",
  "accountId": "acct-customer-1",
  "beneficiaryId": "ben-1",
  "amount": 999999.00,
  "currency": "GBP",
  "reference": "too-much"
}
```

### Expected Response

**Status Code:** `400 Bad Request`

```json
{ "title": "Bad Request", "status": 400, "detail": "Insufficient funds", "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc" }
```

---

## Scenario 138 — Lab — Scenarios without auth

**URL:** `http://localhost:8083/v1/scenarios`  
**Method:** `GET`  
**Authentication:** None

### Headers

| Header | Value |
|--------|-------|
| Accept | `application/json` |

### Request Body

None

### Expected Response

**Status Code:** `401 Unauthorized`

```json
{ "title": "Unauthorized", "status": 401, "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc" }
```

---
