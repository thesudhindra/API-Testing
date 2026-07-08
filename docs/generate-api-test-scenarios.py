#!/usr/bin/env python3
"""Generate docs/API-TEST-SCENARIOS.md and docs/postman/playground-api-collection.json."""

import json
import uuid
from pathlib import Path

BASE = {
    "platform": "http://localhost:8080",
    "banking": "http://localhost:8081",
    "enterprise": "http://localhost:8082",
    "lab": "http://localhost:8083",
}

BASIC = {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy", "Content-Type": "application/json"}
BEARER_CUSTOMER = {
    "Authorization": "Bearer {{jwt_token}}",
    "X-Tenant-Id": "tenant-demo",
    "Content-Type": "application/json",
}
BEARER_OPS = {
    "Authorization": "Bearer {{admin_jwt}}",
    "X-Tenant-Id": "tenant-demo",
    "Content-Type": "application/json",
}
IDEM = {"Idempotency-Key": "{{idempotency_key}}"}
EXAMPLE_UUID = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
EXAMPLE_UUID_2 = "b2c3d4e5-f6a7-8901-bcde-f12345678901"
CORRELATION_ID = "c1d2e3f4-a5b6-7890-cdef-123456789abc"
QUOTE_UUID = "c3d4e5f6-a7b8-9012-cdef-345678901234"
JWT_EXAMPLE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLWN1c3RvbWVyIiwidGVuYW50SWQiOiJ0ZW5hbnQtZGVtbyJ9.SIGNATURE"

# Reusable full list-item examples (no vague placeholders)
PAYMENT_ITEM = (
    '{\n    "id": "' + EXAMPLE_UUID + '",\n'
    '    "transactionId": "' + EXAMPLE_UUID_2 + '",\n'
    '    "partyId": "party-customer-1",\n'
    '    "accountId": "acct-customer-1",\n'
    '    "beneficiaryId": "ben-1",\n'
    '    "amount": 25.50,\n'
    '    "currency": "GBP",\n'
    '    "reference": "rent",\n'
    '    "status": "COMPLETED",\n'
    '    "createdAt": "2026-07-08T12:00:00Z"\n'
    '  }'
)
TRANSFER_ITEM = (
    '{\n    "id": "' + EXAMPLE_UUID + '",\n'
    '    "transactionId": "' + EXAMPLE_UUID_2 + '",\n'
    '    "fromAccountId": "acct-customer-1",\n'
    '    "toAccountId": "acct-customer-2",\n'
    '    "amount": 50.00,\n'
    '    "currency": "GBP",\n'
    '    "reference": "savings",\n'
    '    "status": "COMPLETED"\n'
    '  }'
)
LOAN_ITEM = (
    '{\n    "id": "' + EXAMPLE_UUID + '",\n'
    '    "partyId": "party-customer-1",\n'
    '    "accountId": "acct-customer-1",\n'
    '    "productCode": "PERSONAL",\n'
    '    "principal": 10000.00,\n'
    '    "currency": "GBP",\n'
    '    "interestRate": 5.5,\n'
    '    "termMonths": 24,\n'
    '    "status": "ACTIVE",\n'
    '    "outstandingBalance": 10000.00\n'
    '  }'
)
FD_ITEM = (
    '{\n    "id": "' + EXAMPLE_UUID + '",\n'
    '    "partyId": "party-customer-1",\n'
    '    "principal": 5000.00,\n'
    '    "currency": "GBP",\n'
    '    "termDays": 365,\n'
    '    "status": "ACTIVE",\n'
    '    "maturityDate": "2027-07-08"\n'
    '  }'
)
RD_ITEM = (
    '{\n    "id": "' + EXAMPLE_UUID + '",\n'
    '    "partyId": "party-customer-1",\n'
    '    "installmentAmount": 200.00,\n'
    '    "currency": "GBP",\n'
    '    "frequency": "MONTHLY",\n'
    '    "status": "ACTIVE"\n'
    '  }'
)
NOTIFICATION_ITEM = (
    '{\n    "id": "' + EXAMPLE_UUID + '",\n'
    '    "partyId": "party-customer-1",\n'
    '    "channel": "EMAIL",\n'
    '    "subject": "Loan approved",\n'
    '    "status": "UNREAD"\n'
    '  }'
)
DOCUMENT_ITEM = (
    '{\n    "id": "' + EXAMPLE_UUID + '",\n'
    '    "partyId": "party-customer-1",\n'
    '    "documentType": "PASSPORT",\n'
    '    "fileName": "passport.pdf",\n'
    '    "status": "UPLOADED"\n'
    '  }'
)
REPORT_ITEM = (
    '{\n    "id": "' + EXAMPLE_UUID + '",\n'
    '    "reportType": "LOAN_PORTFOLIO",\n'
    '    "status": "COMPLETED",\n'
    '    "resultLocation": "/reports/' + EXAMPLE_UUID + '.csv"\n'
    '  }'
)
ALERT_ITEM = (
    '{\n    "id": "' + EXAMPLE_UUID + '",\n'
    '    "partyId": "party-customer-1",\n'
    '    "entityType": "PAYMENT",\n'
    '    "entityId": "pay-lab-1",\n'
    '    "ruleCode": "VELOCITY",\n'
    '    "riskScore": 85,\n'
    '    "status": "OPEN",\n'
    '    "details": "Amount exceeds velocity threshold"\n'
    '  }'
)
AML_CASE_ITEM = (
    '{\n    "id": "' + EXAMPLE_UUID + '",\n'
    '    "partyId": "party-customer-1",\n'
    '    "caseType": "SANCTIONS",\n'
    '    "status": "OPEN",\n'
    '    "priority": "HIGH"\n'
    '  }'
)
JOB_ITEM = (
    '{\n    "id": "' + EXAMPLE_UUID + '",\n'
    '    "jobType": "REPORT_GENERATION",\n'
    '    "status": "COMPLETED",\n'
    '    "progress": 100\n'
    '  }'
)
WEBHOOK_ITEM = (
    '{\n    "id": "' + EXAMPLE_UUID + '",\n'
    '    "eventType": "FRAUD_ALERT_CREATED",\n'
    '    "targetUrl": "http://localhost:8083/v1/mocks/webhooks/fail",\n'
    '    "status": "ACTIVE"\n'
    '  }'
)

SCENARIO_DETAILS = {
    "payment-happy-path": """{
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
}""",
    "idempotency-replay": """{
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
}""",
    "fraud-velocity-alert": """{
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
}""",
    "webhook-retry-dlq": """{
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
}""",
    "concurrent-transfer-race": """{
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
}""",
    "bola-party-access": """{
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
}""",
}

scenarios = []
n = 0


def add(name, module, method, path, auth, headers, body, status, response, notes=""):
    global n
    n += 1
    url = BASE[module] + path
    scenarios.append({
        "num": n,
        "name": name,
        "module": module,
        "path": path,
        "url": url,
        "method": method,
        "auth": auth,
        "headers": headers,
        "body": body,
        "status": status,
        "response": response,
        "notes": notes,
    })


# --- PLATFORM ---
add("Platform — Get health", "platform", "GET", "/health", "None", {"Accept": "application/json"}, None,
    "200 OK", '{\n  "status": "UP",\n  "service": "platform-api",\n  "components": { "database": "UP" }\n}')

add("Platform — Get version", "platform", "GET", "/version", "None", {"Accept": "application/json"}, None,
    "200 OK", '{\n  "name": "platform-api",\n  "version": "0.1.0-SNAPSHOT"\n}')

add("Platform — Demo bad request error", "platform", "GET", "/v1/demo/errors/bad-request", "Basic (learner/learner)",
    {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "400 Bad Request",
    '{\n  "type": "https://playground.example/problems/bad-request",\n  "title": "Bad Request",\n  "status": 400,\n  "detail": "Demonstration bad request",\n  "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc"\n}')

add("Platform — Demo not found error", "platform", "GET", "/v1/demo/errors/not-found", "Basic (learner/learner)",
    {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "404 Not Found",
    '{\n  "type": "https://playground.example/problems/not-found",\n  "title": "Not Found",\n  "status": 404,\n  "detail": "Demonstration not found",\n  "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc"\n}')

add("Platform — Demo internal error", "platform", "GET", "/v1/demo/errors/internal", "Basic (learner/learner)",
    {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "500 Internal Server Error",
    '{\n  "type": "https://playground.example/problems/internal-server-error",\n  "title": "Internal Server Error",\n  "status": 500,\n  "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc"\n}')

add("Platform — Validate body (valid)", "platform", "POST", "/v1/demo/validate", "Basic (learner/learner)",
    {**BASIC}, '{\n  "name": "Jane"\n}', "200 OK",
    '{\n  "message": "Validation passed",\n  "name": "Jane"\n}')

add("Platform — Validate body (invalid)", "platform", "POST", "/v1/demo/validate", "Basic (learner/learner)",
    {**BASIC}, '{\n  "name": ""\n}', "422 Unprocessable Entity",
    '{\n  "title": "Validation Failed",\n  "status": 422,\n  "errors": [{ "field": "name", "message": "name must not be blank" }],\n  "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc"\n}')

# --- BANKING AUTH ---
add("Banking — Login as customer", "banking", "POST", "/v1/auth/login", "None",
    {"Content-Type": "application/json"},
    '{\n  "tenantId": "tenant-demo",\n  "username": "customer",\n  "password": "password"\n}',
    "200 OK",
    '{\n  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLWN1c3RvbWVyIiwidGVuYW50SWQiOiJ0ZW5hbnQtZGVtbyJ9.SIGNATURE",\n  "tokenType": "Bearer",\n  "expiresIn": 3600\n}',
    "Save accessToken as jwt_token in Postman.")

add("Banking — Login as ops", "banking", "POST", "/v1/auth/login", "None",
    {"Content-Type": "application/json"},
    '{\n  "tenantId": "tenant-demo",\n  "username": "ops",\n  "password": "password"\n}',
    "200 OK", '{\n  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLWN1c3RvbWVyIiwidGVuYW50SWQiOiJ0ZW5hbnQtZGVtbyJ9.SIGNATURE",\n  "tokenType": "Bearer",\n  "expiresIn": 3600\n}')

add("Banking — Login as admin", "banking", "POST", "/v1/auth/login", "None",
    {"Content-Type": "application/json"},
    '{\n  "tenantId": "tenant-demo",\n  "username": "admin",\n  "password": "password"\n}',
    "200 OK", '{\n  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLWN1c3RvbWVyIiwidGVuYW50SWQiOiJ0ZW5hbnQtZGVtbyJ9.SIGNATURE",\n  "tokenType": "Bearer",\n  "expiresIn": 3600\n}')

add("Banking — Get current user", "banking", "GET", "/v1/auth/me", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK",
    '{\n  "id": "user-customer",\n  "tenantId": "tenant-demo",\n  "username": "customer",\n  "partyId": "party-customer-1",\n  "enabled": true,\n  "roles": ["RETAIL_CUSTOMER"]\n}')

add("Banking — Get health", "banking", "GET", "/health", "None", {"Accept": "application/json"}, None,
    "200 OK", '{\n  "status": "UP",\n  "service": "banking-api",\n  "components": { "database": "UP" }\n}')

add("Banking — Get version", "banking", "GET", "/version", "None", {"Accept": "application/json"}, None,
    "200 OK", '{\n  "name": "banking-api",\n  "version": "0.1.0-SNAPSHOT"\n}')

# --- BANKING ROLES USERS ---
add("Banking — List roles", "banking", "GET", "/v1/roles", "Bearer JWT", BEARER_CUSTOMER, None, "200 OK",
    '[\n  { "id": "role-customer", "tenantId": "tenant-demo", "name": "RETAIL_CUSTOMER" },\n  { "id": "role-ops", "tenantId": "tenant-demo", "name": "OPS_AGENT" },\n  { "id": "role-admin", "tenantId": "tenant-demo", "name": "ADMIN" }\n]')

add("Banking — List users", "banking", "GET", "/v1/users?page=0&size=20", "Bearer JWT (ops/admin)", BEARER_OPS, None,
    "200 OK",
    '{\n  "items": [{ "id": "user-customer", "username": "customer", "partyId": "party-customer-1", "enabled": true, "roles": ["RETAIL_CUSTOMER"] }],\n  "page": 0, "size": 20, "totalElements": 3, "totalPages": 1, "nextCursor": null\n}')

add("Banking — Get user by id", "banking", "GET", "/v1/users/user-customer", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK",
    '{\n  "id": "user-customer",\n  "tenantId": "tenant-demo",\n  "username": "customer",\n  "partyId": "party-customer-1",\n  "enabled": true,\n  "roles": ["RETAIL_CUSTOMER"]\n}')

add("Banking — Create user", "banking", "POST", "/v1/users", "Bearer JWT (admin)", BEARER_OPS,
    '{\n  "username": "newuser",\n  "password": "password123",\n  "partyId": "party-customer-1",\n  "roleNames": ["RETAIL_CUSTOMER"]\n}',
    "201 Created",
    '{\n  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",\n  "tenantId": "tenant-demo",\n  "username": "newuser",\n  "partyId": "party-customer-1",\n  "enabled": true,\n  "roles": ["RETAIL_CUSTOMER"]\n}')

# --- PARTIES ---
add("Banking — Get party", "banking", "GET", "/v1/parties/party-customer-1", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK",
    '{\n  "id": "party-customer-1",\n  "tenantId": "tenant-demo",\n  "partyType": "INDIVIDUAL",\n  "status": "ACTIVE",\n  "firstName": "Jane",\n  "lastName": "Doe",\n  "email": "jane.doe@example.com",\n  "version": 0\n}')

add("Banking — List parties", "banking", "GET", "/v1/parties?page=0&size=20", "Bearer JWT (ops/admin)", BEARER_OPS, None,
    "200 OK",
    '{\n  "items": [{ "id": "party-customer-1", "firstName": "Jane", "lastName": "Doe", "status": "ACTIVE" }],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')

add("Banking — Create party", "banking", "POST", "/v1/parties", "Bearer JWT (ops/admin)", BEARER_OPS,
    '{\n  "partyType": "INDIVIDUAL",\n  "firstName": "John",\n  "lastName": "Smith",\n  "email": "john.smith@example.com"\n}',
    "201 Created",
    '{\n  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",\n  "tenantId": "tenant-demo",\n  "partyType": "INDIVIDUAL",\n  "status": "PROSPECT",\n  "firstName": "John",\n  "lastName": "Smith",\n  "email": "john.smith@example.com"\n}')

add("Banking — Update party", "banking", "PATCH", "/v1/parties/party-customer-1", "Bearer JWT", BEARER_CUSTOMER,
    '{\n  "firstName": "Jane",\n  "email": "jane.updated@example.com"\n}', "200 OK",
    '{\n  "id": "party-customer-1",\n  "firstName": "Jane",\n  "email": "jane.updated@example.com",\n  "status": "ACTIVE"\n}')

add("Banking — BOLA get other party (negative)", "banking", "GET", "/v1/parties/party-other", "Bearer JWT (customer)", BEARER_CUSTOMER, None,
    "403 Forbidden",
    '{\n  "title": "Forbidden",\n  "status": 403,\n  "detail": "Access to this party is not permitted",\n  "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc"\n}')

# --- KYC ---
add("Banking — List KYC cases", "banking", "GET", "/v1/kyc-cases?partyId=party-customer-1", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '[{ "id": "kyc-1", "partyId": "party-customer-1", "status": "APPROVED", "level": "STANDARD" }]')

add("Banking — Get KYC case", "banking", "GET", "/v1/kyc-cases/kyc-1", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{ "id": "kyc-1", "partyId": "party-customer-1", "status": "APPROVED", "level": "STANDARD" }')

add("Banking — Submit KYC case", "banking", "POST", "/v1/kyc-cases", "Bearer JWT", BEARER_CUSTOMER,
    '{ "partyId": "party-customer-1" }', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "status": "OPEN", "level": "STANDARD" }',
    "Fails 409 if open case already exists for party.")

add("Banking — Review KYC case", "banking", "POST", "/v1/kyc-cases/kyc-1/review", "Bearer JWT (ops/admin)", BEARER_OPS,
    '{\n  "status": "APPROVED",\n  "decisionReason": "Documents verified"\n}', "200 OK",
    '{ "id": "kyc-1", "status": "APPROVED", "decisionReason": "Documents verified" }')

# --- ACCOUNTS ---
add("Banking — List accounts", "banking", "GET", "/v1/accounts?partyId=party-customer-1&page=0&size=20", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK",
    '{\n  "items": [\n    { "id": "acct-customer-1", "accountNumber": "GB1234567890", "currency": "GBP", "productCode": "CURRENT", "status": "ACTIVE", "availableBalance": 5000.00, "ledgerBalance": 5000.00 },\n    { "id": "acct-customer-2", "accountNumber": "GB9876543210", "currency": "GBP", "productCode": "SAVINGS", "status": "ACTIVE", "availableBalance": 1000.00, "ledgerBalance": 1000.00 }\n  ],\n  "page": 0, "size": 20, "totalElements": 3, "totalPages": 1, "nextCursor": null\n}')

add("Banking — Get account", "banking", "GET", "/v1/accounts/acct-customer-1", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK",
    '{\n  "id": "acct-customer-1",\n  "tenantId": "tenant-demo",\n  "partyId": "party-customer-1",\n  "accountNumber": "GB1234567890",\n  "currency": "GBP",\n  "productCode": "CURRENT",\n  "status": "ACTIVE",\n  "availableBalance": 5000.00,\n  "ledgerBalance": 5000.00,\n  "version": 0\n}')

add("Banking — Open account", "banking", "POST", "/v1/accounts", "Bearer JWT", BEARER_CUSTOMER,
    '{\n  "partyId": "party-customer-1",\n  "currency": "GBP",\n  "productCode": "CURRENT"\n}', "201 Created",
    '{\n  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",\n  "partyId": "party-customer-1",\n  "currency": "GBP",\n  "productCode": "CURRENT",\n  "status": "ACTIVE",\n  "availableBalance": 0.00,\n  "ledgerBalance": 0.00\n}')

# --- BENEFICIARIES ---
add("Banking — List beneficiaries", "banking", "GET", "/v1/beneficiaries?partyId=party-customer-1", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK",
    '[{ "id": "ben-1", "partyId": "party-customer-1", "nickname": "Savings Pot", "status": "ACTIVE", "sortCode": "12-34-56", "accountNumber": "87654321" }]')

add("Banking — Add beneficiary", "banking", "POST", "/v1/beneficiaries", "Bearer JWT", BEARER_CUSTOMER,
    '{\n  "partyId": "party-customer-1",\n  "nickname": "Landlord",\n  "sortCode": "11-22-33",\n  "accountNumber": "12345678"\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "nickname": "Landlord", "status": "PENDING_VERIFICATION" }')

add("Banking — Delete beneficiary", "banking", "DELETE", "/v1/beneficiaries/ben-1", "Bearer JWT", BEARER_CUSTOMER, None,
    "204 No Content", "NO_BODY")

# --- PAYMENTS ---
pay_body = '{\n  "partyId": "party-customer-1",\n  "accountId": "acct-customer-1",\n  "beneficiaryId": "ben-1",\n  "amount": 25.50,\n  "currency": "GBP",\n  "reference": "rent"\n}'
pay_resp = '{\n  "id": "' + EXAMPLE_UUID + '",\n  "transactionId": "' + EXAMPLE_UUID_2 + '",\n  "partyId": "party-customer-1",\n  "accountId": "acct-customer-1",\n  "beneficiaryId": "ben-1",\n  "amount": 25.50,\n  "currency": "GBP",\n  "reference": "rent",\n  "status": "COMPLETED",\n  "createdAt": "2026-07-08T12:00:00Z"\n}'
add("Banking — Create payment", "banking", "POST", "/v1/payments", "Bearer JWT + Idempotency-Key",
    {**BEARER_CUSTOMER, **IDEM}, pay_body, "201 Created", pay_resp)

add("Banking — Get payment", "banking", "GET", f"/v1/payments/{EXAMPLE_UUID}", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", pay_resp)

add("Banking — List payments", "banking", "GET", "/v1/payments?partyId=party-customer-1&page=0&size=20", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{\n  "items": [' + PAYMENT_ITEM + '],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')

add("Banking — Payment without auth (negative)", "banking", "POST", "/v1/payments", "None",
    {"Content-Type": "application/json", **IDEM}, pay_body, "401 Unauthorized",
    '{\n  "title": "Unauthorized",\n  "status": 401,\n  "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc"\n}')

# --- TRANSFERS ---
xfer_body = '{\n  "fromAccountId": "acct-customer-1",\n  "toAccountId": "acct-customer-2",\n  "amount": 50.00,\n  "currency": "GBP",\n  "reference": "savings"\n}'
xfer_resp = '{\n  "id": "' + EXAMPLE_UUID + '",\n  "transactionId": "' + EXAMPLE_UUID_2 + '",\n  "fromAccountId": "acct-customer-1",\n  "toAccountId": "acct-customer-2",\n  "amount": 50.00,\n  "currency": "GBP",\n  "reference": "savings",\n  "status": "COMPLETED"\n}'
add("Banking — Create transfer", "banking", "POST", "/v1/transfers", "Bearer JWT + Idempotency-Key",
    {**BEARER_CUSTOMER, **IDEM}, xfer_body, "201 Created", xfer_resp)

add("Banking — Get transfer", "banking", "GET", f"/v1/transfers/{EXAMPLE_UUID}", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", xfer_resp)

add("Banking — List transfers", "banking", "GET", "/v1/transfers?accountId=acct-customer-1&page=0&size=20", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{\n  "items": [' + TRANSFER_ITEM + '],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')

# --- CARDS ---
add("Banking — Issue card", "banking", "POST", "/v1/cards", "Bearer JWT", BEARER_CUSTOMER,
    '{\n  "partyId": "party-customer-1",\n  "accountId": "acct-customer-1",\n  "productCode": "DEBIT"\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "accountId": "acct-customer-1", "panLast4": "1234", "productCode": "DEBIT", "status": "ACTIVE" }')

add("Banking — List cards", "banking", "GET", "/v1/cards?partyId=party-customer-1", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '[{ "id": "card-1", "partyId": "party-customer-1", "accountId": "acct-customer-1", "panLast4": "4242", "productCode": "DEBIT", "status": "ACTIVE" }]')

add("Banking — Get card", "banking", "GET", "/v1/cards/card-1", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{ "id": "card-1", "panLast4": "4242", "status": "ACTIVE" }')

add("Banking — Authorize card", "banking", "POST", "/v1/cards/card-1/authorizations", "Bearer JWT + Idempotency-Key",
    {**BEARER_CUSTOMER, **IDEM},
    '{\n  "merchantName": "Coffee Shop",\n  "amount": 4.50,\n  "currency": "GBP"\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "cardId": "card-1", "merchantName": "Coffee Shop", "amount": 4.50, "currency": "GBP", "status": "APPROVED" }')

# --- FX ---
add("Banking — Create FX quote", "banking", "POST", "/v1/fx/quotes", "Bearer JWT", BEARER_CUSTOMER,
    '{\n  "fromCurrency": "GBP",\n  "toCurrency": "EUR",\n  "fromAmount": 100.00\n}', "201 Created",
    '{ "id": "c3d4e5f6-a7b8-9012-cdef-345678901234", "fromCurrency": "GBP", "toCurrency": "EUR", "rate": 1.17, "fromAmount": 100.00, "toAmount": 117.00, "expiresAt": "2026-07-08T12:05:00Z" }')

add("Banking — Execute FX conversion", "banking", "POST", "/v1/fx/conversions", "Bearer JWT + Idempotency-Key",
    {**BEARER_CUSTOMER, **IDEM},
    '{\n  "quoteId": "c3d4e5f6-a7b8-9012-cdef-345678901234",\n  "fromAccountId": "acct-customer-1",\n  "toAccountId": "acct-customer-eur",\n  "partyId": "party-customer-1"\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "quoteId": "c3d4e5f6-a7b8-9012-cdef-345678901234", "fromAmount": 100.00, "toAmount": 117.00, "fromCurrency": "GBP", "toCurrency": "EUR", "rate": 1.17, "status": "COMPLETED" }')

add("Banking — Get FX conversion", "banking", "GET", f"/v1/fx/conversions/{EXAMPLE_UUID}", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{ "id": "' + EXAMPLE_UUID + '", "fromCurrency": "GBP", "toCurrency": "EUR", "rate": 1.17, "fromAmount": 100.00, "toAmount": 117.00, "status": "COMPLETED" }')

# --- TRANSACTIONS LEDGER STATEMENTS ---
add("Banking — List transactions", "banking", "GET", "/v1/transactions?accountId=acct-customer-1&page=0&size=20", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{\n  "items": [{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "txnType": "PAYMENT", "status": "COMPLETED", "amount": 25.50, "currency": "GBP" }],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')

add("Banking — Get transaction", "banking", "GET", f"/v1/transactions/{EXAMPLE_UUID_2}", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{ "id": "' + EXAMPLE_UUID_2 + '", "txnType": "PAYMENT", "status": "COMPLETED", "amount": 25.50, "currency": "GBP", "accountId": "acct-customer-1" }')

add("Banking — List ledger entries", "banking", "GET", "/v1/ledger-entries?accountId=acct-customer-1&page=0&size=20", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{\n  "items": [{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "entryType": "DEBIT", "amount": 25.50, "currency": "GBP", "balanceAfter": 4974.50 }],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')

add("Banking — Generate statement", "banking", "POST", "/v1/accounts/acct-customer-1/statements", "Bearer JWT", BEARER_CUSTOMER,
    '{\n  "periodStart": "2026-01-01",\n  "periodEnd": "2026-01-31"\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "accountId": "acct-customer-1", "periodStart": "2026-01-01", "periodEnd": "2026-01-31", "openingBalance": 5000.00, "closingBalance": 4974.50, "currency": "GBP", "status": "PUBLISHED" }')

add("Banking — List statements", "banking", "GET", "/v1/accounts/acct-customer-1/statements?page=0&size=20", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{\n  "items": [{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "periodStart": "2026-01-01", "periodEnd": "2026-01-31", "status": "PUBLISHED" }],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')

add("Banking — Get statement", "banking", "GET", f"/v1/statements/{EXAMPLE_UUID}", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{ "id": "' + EXAMPLE_UUID + '", "accountId": "acct-customer-1", "periodStart": "2026-01-01", "periodEnd": "2026-01-31", "openingBalance": 5000.00, "closingBalance": 4974.50, "currency": "GBP", "lines": [{ "description": "Payment rent", "amount": -25.50, "postedAt": "2026-07-08T12:00:00Z" }] }')

# --- ENTERPRISE STATUS ---
add("Enterprise — Get health", "enterprise", "GET", "/health", "None", {"Accept": "application/json"}, None,
    "200 OK", '{\n  "status": "UP",\n  "service": "enterprise-api",\n  "components": { "database": "UP" }\n}')

add("Enterprise — Get version", "enterprise", "GET", "/version", "None", {"Accept": "application/json"}, None,
    "200 OK", '{\n  "name": "enterprise-api",\n  "version": "0.1.0-SNAPSHOT"\n}')

# --- LOANS ---
loan_body = '{\n  "partyId": "party-customer-1",\n  "accountId": "acct-customer-1",\n  "productCode": "PERSONAL",\n  "principal": 10000.00,\n  "currency": "GBP",\n  "interestRate": 5.5,\n  "termMonths": 24\n}'
loan_resp = '{\n  "id": "' + EXAMPLE_UUID + '",\n  "partyId": "party-customer-1",\n  "accountId": "acct-customer-1",\n  "productCode": "PERSONAL",\n  "principal": 10000.00,\n  "currency": "GBP",\n  "interestRate": 5.5,\n  "termMonths": 24,\n  "status": "ACTIVE",\n  "outstandingBalance": 10000.00\n}'
add("Enterprise — Create loan", "enterprise", "POST", "/v1/loans", "Bearer JWT", BEARER_CUSTOMER, loan_body, "201 Created", loan_resp)
add("Enterprise — Get loan", "enterprise", "GET", f"/v1/loans/{EXAMPLE_UUID}", "Bearer JWT", BEARER_CUSTOMER, None, "200 OK", loan_resp)
add("Enterprise — List loans", "enterprise", "GET", "/v1/loans?partyId=party-customer-1&page=0&size=20", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{\n  "items": [' + LOAN_ITEM + '],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')
add("Enterprise — Create loan repayment", "enterprise", "POST", f"/v1/loans/{EXAMPLE_UUID}/repayments", "Bearer JWT", BEARER_CUSTOMER,
    '{ "amount": 500.00 }', "201 Created",
    '{ "id": "' + EXAMPLE_UUID_2 + '", "loanId": "' + EXAMPLE_UUID + '", "amount": 500.00, "currency": "GBP", "status": "COMPLETED" }')

# --- FIXED / RECURRING DEPOSITS ---
fd_body = '{\n  "partyId": "party-customer-1",\n  "accountId": "acct-customer-1",\n  "principal": 5000.00,\n  "currency": "GBP",\n  "interestRate": 4.25,\n  "termDays": 365\n}'
add("Enterprise — Create fixed deposit", "enterprise", "POST", "/v1/fixed-deposits", "Bearer JWT", BEARER_CUSTOMER, fd_body, "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "principal": 5000.00, "currency": "GBP", "termDays": 365, "status": "ACTIVE", "maturityDate": "2027-07-08" }')
add("Enterprise — Get fixed deposit", "enterprise", "GET", f"/v1/fixed-deposits/{EXAMPLE_UUID}", "Bearer JWT", BEARER_CUSTOMER, None, "200 OK",
    '{ "id": "' + EXAMPLE_UUID + '", "partyId": "party-customer-1", "principal": 5000.00, "currency": "GBP", "termDays": 365, "status": "ACTIVE", "maturityDate": "2027-07-08" }')
add("Enterprise — List fixed deposits", "enterprise", "GET", "/v1/fixed-deposits?partyId=party-customer-1&page=0&size=20", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{\n  "items": [' + FD_ITEM + '],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')

rd_body = '{\n  "partyId": "party-customer-1",\n  "accountId": "acct-customer-1",\n  "installmentAmount": 200.00,\n  "currency": "GBP",\n  "frequency": "MONTHLY"\n}'
add("Enterprise — Create recurring deposit", "enterprise", "POST", "/v1/recurring-deposits", "Bearer JWT", BEARER_CUSTOMER, rd_body, "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "installmentAmount": 200.00, "currency": "GBP", "frequency": "MONTHLY", "status": "ACTIVE" }')
add("Enterprise — Get recurring deposit", "enterprise", "GET", f"/v1/recurring-deposits/{EXAMPLE_UUID}", "Bearer JWT", BEARER_CUSTOMER, None, "200 OK",
    '{ "id": "' + EXAMPLE_UUID + '", "partyId": "party-customer-1", "installmentAmount": 200.00, "currency": "GBP", "frequency": "MONTHLY", "status": "ACTIVE" }')
add("Enterprise — List recurring deposits", "enterprise", "GET", "/v1/recurring-deposits?partyId=party-customer-1&page=0&size=20", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{\n  "items": [' + RD_ITEM + '],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')
add("Enterprise — Post RD installment", "enterprise", "POST", f"/v1/recurring-deposits/{EXAMPLE_UUID}/installments", "Bearer JWT", BEARER_CUSTOMER, None, "201 Created",
    '{ "id": "' + EXAMPLE_UUID_2 + '", "recurringDepositId": "' + EXAMPLE_UUID + '", "amount": 200.00, "currency": "GBP", "status": "PAID" }')

# --- NOTIFICATIONS DOCUMENTS REPORTS ---
add("Enterprise — Create notification", "enterprise", "POST", "/v1/notifications", "Bearer JWT (ops/admin)", BEARER_OPS,
    '{\n  "partyId": "party-customer-1",\n  "channel": "EMAIL",\n  "subject": "Loan approved",\n  "body": "Your loan has been approved."\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "channel": "EMAIL", "subject": "Loan approved", "status": "UNREAD" }')
add("Enterprise — List notifications", "enterprise", "GET", "/v1/notifications?partyId=party-customer-1&page=0&size=20", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{\n  "items": [' + NOTIFICATION_ITEM + '],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')
add("Enterprise — Mark notification read", "enterprise", "PATCH", f"/v1/notifications/{EXAMPLE_UUID}/read", "Bearer JWT", BEARER_CUSTOMER, None, "200 OK",
    '{ "id": "' + EXAMPLE_UUID + '", "partyId": "party-customer-1", "channel": "EMAIL", "subject": "Loan approved", "status": "READ", "readAt": "2026-07-08T12:00:00Z" }')

add("Enterprise — Register document", "enterprise", "POST", "/v1/documents", "Bearer JWT", BEARER_CUSTOMER,
    '{\n  "partyId": "party-customer-1",\n  "documentType": "PASSPORT",\n  "fileName": "passport.pdf",\n  "contentType": "application/pdf"\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "documentType": "PASSPORT", "fileName": "passport.pdf", "status": "UPLOADED" }')
add("Enterprise — Get document", "enterprise", "GET", f"/v1/documents/{EXAMPLE_UUID}", "Bearer JWT", BEARER_CUSTOMER, None, "200 OK",
    '{ "id": "' + EXAMPLE_UUID + '", "partyId": "party-customer-1", "documentType": "PASSPORT", "fileName": "passport.pdf", "contentType": "application/pdf", "status": "UPLOADED" }')
add("Enterprise — List documents", "enterprise", "GET", "/v1/documents?partyId=party-customer-1&page=0&size=20", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{\n  "items": [' + DOCUMENT_ITEM + '],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')

add("Enterprise — Generate report", "enterprise", "POST", "/v1/reports", "Bearer JWT (ops/admin)", BEARER_OPS,
    '{\n  "reportType": "LOAN_PORTFOLIO",\n  "parameters": "partyId=party-customer-1"\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "reportType": "LOAN_PORTFOLIO", "status": "PENDING" }')
add("Enterprise — Get report", "enterprise", "GET", f"/v1/reports/{EXAMPLE_UUID}", "Bearer JWT (ops/admin)", BEARER_OPS, None, "200 OK",
    '{ "id": "' + EXAMPLE_UUID + '", "reportType": "LOAN_PORTFOLIO", "status": "COMPLETED", "resultLocation": "/reports/' + EXAMPLE_UUID + '.csv", "generatedAt": "2026-07-08T12:00:00Z" }')
add("Enterprise — List reports", "enterprise", "GET", "/v1/reports?page=0&size=20", "Bearer JWT (ops/admin)", BEARER_OPS, None,
    "200 OK", '{\n  "items": [' + REPORT_ITEM + '],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')

# --- FRAUD AML ---
fraud_body = '{\n  "partyId": "party-customer-1",\n  "entityType": "PAYMENT",\n  "entityId": "pay-lab-1",\n  "amount": 6000.00\n}'
fraud_resp = '{\n  "id": "' + EXAMPLE_UUID + '",\n  "partyId": "party-customer-1",\n  "entityType": "PAYMENT",\n  "entityId": "pay-lab-1",\n  "ruleCode": "VELOCITY",\n  "riskScore": 85,\n  "status": "OPEN",\n  "details": "Amount exceeds velocity threshold"\n}'
add("Enterprise — Fraud screen (alert)", "enterprise", "POST", "/v1/fraud/screen", "Bearer JWT", BEARER_CUSTOMER, fraud_body, "201 Created", fraud_resp)
add("Enterprise — Fraud screen (no alert)", "enterprise", "POST", "/v1/fraud/screen", "Bearer JWT", BEARER_CUSTOMER,
    '{\n  "partyId": "party-customer-1",\n  "entityType": "PAYMENT",\n  "entityId": "pay-lab-2",\n  "amount": 100.00\n}', "200 OK", "NO_BODY",
    "Amount 100.00 is below fraud.velocity.threshold (5000). Response has no JSON body.")
add("Enterprise — List fraud alerts", "enterprise", "GET", "/v1/fraud/alerts?partyId=party-customer-1&page=0&size=20", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{\n  "items": [' + ALERT_ITEM + '],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')
add("Enterprise — Review fraud alert", "enterprise", "PATCH", f"/v1/fraud/alerts/{EXAMPLE_UUID}/review", "Bearer JWT (ops/admin)", BEARER_OPS,
    '{ "status": "REVIEWED" }', "200 OK", '{ "id": "' + EXAMPLE_UUID + '", "partyId": "party-customer-1", "status": "REVIEWED", "reviewedAt": "2026-07-08T12:00:00Z" }')

add("Enterprise — Create AML case", "enterprise", "POST", "/v1/aml/cases", "Bearer JWT (ops/admin)", BEARER_OPS,
    '{\n  "partyId": "party-customer-1",\n  "caseType": "SANCTIONS",\n  "priority": "HIGH"\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "caseType": "SANCTIONS", "status": "OPEN", "priority": "HIGH" }')
add("Enterprise — Get AML case", "enterprise", "GET", f"/v1/aml/cases/{EXAMPLE_UUID}", "Bearer JWT", BEARER_CUSTOMER, None, "200 OK",
    '{ "id": "' + EXAMPLE_UUID + '", "partyId": "party-customer-1", "caseType": "SANCTIONS", "status": "OPEN", "priority": "HIGH" }')
add("Enterprise — List AML cases", "enterprise", "GET", "/v1/aml/cases?partyId=party-customer-1&page=0&size=20", "Bearer JWT", BEARER_CUSTOMER, None,
    "200 OK", '{\n  "items": [' + AML_CASE_ITEM + '],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')
add("Enterprise — Submit AML screening", "enterprise", "POST", "/v1/aml/screenings", "Bearer JWT", BEARER_CUSTOMER,
    '{\n  "partyId": "party-customer-1",\n  "screeningType": "SANCTIONS"\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "partyId": "party-customer-1", "screeningType": "SANCTIONS", "result": "CLEAR", "matchScore": 0 }')

# --- ADMIN AUDIT EVENTS JOBS ---
add("Enterprise — List admin settings", "enterprise", "GET", "/v1/admin/settings", "Bearer JWT (ops/admin)", BEARER_OPS, None, "200 OK",
    '[{ "settingKey": "fraud.velocity.threshold", "settingValue": "5000", "updatedBy": "user-admin" }, { "settingKey": "aml.auto_escalate_score", "settingValue": "80", "updatedBy": "user-admin" }]')
add("Enterprise — Get admin setting", "enterprise", "GET", "/v1/admin/settings/fraud.velocity.threshold", "Bearer JWT (ops/admin)", BEARER_OPS, None, "200 OK",
    '{ "settingKey": "fraud.velocity.threshold", "settingValue": "5000", "updatedBy": "user-admin" }')
add("Enterprise — Update admin setting", "enterprise", "PUT", "/v1/admin/settings/fraud.velocity.threshold", "Bearer JWT (ops/admin)", BEARER_OPS,
    '{ "settingValue": "6000" }', "200 OK", '{ "settingKey": "fraud.velocity.threshold", "settingValue": "6000" }')
add("Enterprise — Admin as customer (negative)", "enterprise", "GET", "/v1/admin/settings", "Bearer JWT (customer)", BEARER_CUSTOMER, None, "403 Forbidden",
    '{ "title": "Forbidden", "status": 403, "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc" }')

add("Enterprise — List audit events", "enterprise", "GET", "/v1/audit/events?page=0&size=20", "Bearer JWT (ops/admin)", BEARER_OPS, None, "200 OK",
    '{\n  "items": [{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "entityType": "PAYMENT", "action": "CREATE", "actorId": "user-customer", "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc" }],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')
add("Enterprise — List domain events", "enterprise", "GET", "/v1/events?page=0&size=20", "Bearer JWT (ops/admin)", BEARER_OPS, None, "200 OK",
    '{\n  "items": [{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "eventType": "FRAUD_ALERT_CREATED", "status": "PUBLISHED" }],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')

add("Enterprise — Submit job", "enterprise", "POST", "/v1/jobs", "Bearer JWT (ops/admin)", BEARER_OPS,
    '{\n  "jobType": "REPORT_GENERATION",\n  "payload": "reportId=abc-123"\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "jobType": "REPORT_GENERATION", "status": "PENDING", "progress": 0 }')
add("Enterprise — Get job", "enterprise", "GET", f"/v1/jobs/{EXAMPLE_UUID}", "Bearer JWT (ops/admin)", BEARER_OPS, None, "200 OK",
    '{ "id": "' + EXAMPLE_UUID + '", "jobType": "REPORT_GENERATION", "status": "COMPLETED", "progress": 100, "completedAt": "2026-07-08T12:01:00Z" }')
add("Enterprise — List jobs", "enterprise", "GET", "/v1/jobs?page=0&size=20", "Bearer JWT (ops/admin)", BEARER_OPS, None, "200 OK",
    '{\n  "items": [' + JOB_ITEM + '],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')

add("Enterprise — List scheduler tasks", "enterprise", "GET", "/v1/scheduler/tasks?page=0&size=20", "Bearer JWT", BEARER_CUSTOMER, None, "200 OK",
    '{\n  "items": [{ "id": "sched-rd-reminder", "taskType": "RD_INSTALLMENT_REMINDER", "cronExpression": "0 0 9 * * *", "enabled": true }],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')
add("Enterprise — Register scheduler task", "enterprise", "POST", "/v1/scheduler/tasks", "Bearer JWT (ops/admin)", BEARER_OPS,
    '{\n  "taskType": "RD_INSTALLMENT_REMINDER",\n  "cronExpression": "0 0 9 * * *",\n  "payload": "{\\"partyId\\":\\"party-customer-1\\"}"\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "taskType": "RD_INSTALLMENT_REMINDER", "enabled": true }')

add("Enterprise — Register webhook", "enterprise", "POST", "/v1/webhooks", "Bearer JWT (ops/admin)", BEARER_OPS,
    '{\n  "eventType": "FRAUD_ALERT_CREATED",\n  "targetUrl": "http://localhost:8083/v1/mocks/webhooks/fail",\n  "secret": "my-hmac-secret"\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "eventType": "FRAUD_ALERT_CREATED", "targetUrl": "http://localhost:8083/v1/mocks/webhooks/fail", "status": "ACTIVE" }')
add("Enterprise — List webhooks", "enterprise", "GET", "/v1/webhooks?page=0&size=20", "Bearer JWT (ops/admin)", BEARER_OPS, None, "200 OK",
    '{\n  "items": [' + WEBHOOK_ITEM + '],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')
add("Enterprise — List webhook deliveries", "enterprise", "GET", f"/v1/webhooks/{EXAMPLE_UUID}/deliveries?page=0&size=20", "Bearer JWT (ops/admin)", BEARER_OPS, None, "200 OK",
    '{\n  "items": [{ "id": "' + EXAMPLE_UUID_2 + '", "webhookId": "' + EXAMPLE_UUID + '", "status": "DLQ", "attemptCount": 3, "httpStatus": 500, "lastAttemptAt": "2026-07-08T12:00:00Z" }],\n  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "nextCursor": null\n}')

add("Enterprise — List circuit breakers", "enterprise", "GET", "/v1/resilience/circuit-breakers", "Bearer JWT (ops/admin)", BEARER_OPS, None, "200 OK",
    '[{ "name": "webhook-delivery", "state": "CLOSED", "failureCount": 0, "successCount": 10 }]')
add("Enterprise — Get circuit breaker", "enterprise", "GET", "/v1/resilience/circuit-breakers/webhook-delivery", "Bearer JWT (ops/admin)", BEARER_OPS, None, "200 OK",
    '{ "name": "webhook-delivery", "state": "CLOSED", "failureCount": 0, "successCount": 10 }')

# --- TEST LAB ---
add("Lab — Get health", "lab", "GET", "/health", "None", {"Accept": "application/json"}, None,
    "200 OK", '{\n  "status": "UP",\n  "service": "playground-api",\n  "components": { "database": "UP" }\n}')
add("Lab — Get version", "lab", "GET", "/version", "None", {"Accept": "application/json"}, None,
    "200 OK", '{\n  "name": "playground-api",\n  "version": "0.1.0-SNAPSHOT"\n}')

add("Lab — Reset all data", "lab", "POST", "/v1/playground/reset", "Basic (learner/learner)", BASIC,
    '{ "scope": "ALL" }', "200 OK",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "scope": "ALL", "status": "COMPLETED", "details": "Playground lab tables cleared; Banking transactional data cleared; Enterprise transactional data cleared" }')

add("Lab — List scenarios", "lab", "GET", "/v1/scenarios", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    '[{ "slug": "payment-happy-path", "title": "Payment Happy Path", "difficulty": "L1" }, { "slug": "idempotency-replay", "title": "Idempotency Replay", "difficulty": "L2" }]')
add("Lab — Get scenario payment-happy-path", "lab", "GET", "/v1/scenarios/payment-happy-path", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    SCENARIO_DETAILS["payment-happy-path"])
add("Lab — Start scenario run", "lab", "POST", "/v1/scenarios/payment-happy-path/runs", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "201 Created",
    '{ "id": "' + EXAMPLE_UUID + '", "scenarioSlug": "payment-happy-path", "status": "RUNNING", "startedAt": "2026-07-08T12:00:00Z", "completedAt": null }')
add("Lab — Complete scenario run", "lab", "PATCH", f"/v1/scenarios/runs/{EXAMPLE_UUID}", "Basic (learner/learner)", {**BASIC},
    '{ "status": "COMPLETED" }', "200 OK",
    '{ "id": "' + EXAMPLE_UUID + '", "scenarioSlug": "payment-happy-path", "status": "COMPLETED", "startedAt": "2026-07-08T12:00:00Z", "completedAt": "2026-07-08T12:05:00Z" }')

add("Lab — List contracts", "lab", "GET", "/v1/contracts", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    '[{ "service": "banking-api", "filename": "openapi.yaml" }, { "service": "enterprise-api", "filename": "openapi.yaml" }]')
add("Lab — Get contract file", "lab", "GET", "/v1/contracts/banking-api/openapi.yaml", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    '{\n  "service": "banking-api",\n  "filename": "openapi.yaml",\n  "content": "openapi: 3.1.0\\ninfo:\\n  title: Banking API\\n  version: 0.1.0-SNAPSHOT\\nservers:\\n  - url: http://localhost:8081\\npaths:\\n  /v1/auth/login:\\n    post:\\n      summary: Login\\n"\n}')

add("Lab — List config", "lab", "GET", "/v1/playground/config", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    '[{ "key": "default_tenant", "value": "tenant-demo" }, { "key": "reset_enabled", "value": "true" }]')
add("Lab — Get config key", "lab", "GET", "/v1/playground/config/default_tenant", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    '{ "key": "default_tenant", "value": "tenant-demo" }')
add("Lab — Update config", "lab", "PUT", "/v1/playground/config/default_tenant", "Basic (learner/learner)", {**BASIC},
    '{ "value": "tenant-demo" }', "200 OK", '{ "key": "default_tenant", "value": "tenant-demo" }')

add("Lab — List fault rules", "lab", "GET", "/v1/playground/faults", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK", '[]')
add("Lab — Create fault rule", "lab", "POST", "/v1/playground/faults", "Basic (learner/learner)", {**BASIC},
    '{\n  "targetService": "BANKING",\n  "pathPattern": "/v1/payments",\n  "faultType": "LATENCY",\n  "config": { "delayMs": 500 }\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "targetService": "BANKING", "pathPattern": "/v1/payments", "faultType": "LATENCY", "enabled": true }')
add("Lab — Disable fault rule", "lab", "DELETE", f"/v1/playground/faults/{EXAMPLE_UUID}", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "204 No Content", "NO_BODY")

add("Lab — List mocks", "lab", "GET", "/v1/playground/mocks", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    '[{ "path": "/v1/mocks/aml/screen/clear", "httpMethod": "POST", "statusCode": 200 }]')
add("Lab — Create mock", "lab", "POST", "/v1/playground/mocks", "Basic (learner/learner)", {**BASIC},
    '{\n  "path": "/v1/mocks/webhooks/fail",\n  "httpMethod": "POST",\n  "statusCode": 500,\n  "responseBody": "{\\"error\\":\\"fail\\"}",\n  "delayMs": 0,\n  "enabled": true\n}', "201 Created",
    '{ "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "path": "/v1/mocks/webhooks/fail", "statusCode": 500 }')
add("Lab — Call AML clear mock (public)", "lab", "POST", "/v1/mocks/aml/screen/clear", "None",
    {"Content-Type": "application/json"}, '{}', "200 OK", '{ "result": "CLEAR", "matchScore": 0 }')
add("Lab — Call AML review mock (public)", "lab", "POST", "/v1/mocks/aml/screen/review", "None",
    {"Content-Type": "application/json"}, '{}', "200 OK", '{ "result": "REVIEW", "matchScore": 75 }')

add("Lab — Generate test data", "lab", "POST", "/v1/playground/test-data", "Basic (learner/learner)", {**BASIC},
    '{\n  "namespace": "lab-session-1",\n  "profile": "retail-customer"\n}', "201 Created",
    '[{ "namespace": "lab-session-1", "entityType": "PARTY", "entityId": "party-customer-1" }, { "namespace": "lab-session-1", "entityType": "ACCOUNT", "entityId": "acct-customer-1" }]')
add("Lab — List test data handles", "lab", "GET", "/v1/playground/test-data?namespace=lab-session-1", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    '[{ "namespace": "lab-session-1", "entityType": "PARTY", "entityId": "party-customer-1" }]')
add("Lab — Delete test data namespace", "lab", "DELETE", "/v1/playground/test-data/lab-session-1", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "204 No Content", "NO_BODY")

add("Lab — Generate seed", "lab", "POST", "/v1/playground/seed", "Basic (learner/learner)", {**BASIC},
    '{ "profile": "retail-customer" }', "201 Created",
    '{ "profile": "retail-customer", "entities": { "party": "party-customer-1", "account": "acct-customer-1" } }')

add("Lab — Get dashboard", "lab", "GET", "/v1/playground/dashboard", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    '{\n  "serviceHealth": { "playground-api": "UP", "platform-api": "UP", "banking-api": "UP", "enterprise-api": "UP" },\n  "scenarioCount": 6,\n  "activeFaultCount": 0,\n  "recentRuns": [],\n  "configSummary": [{ "key": "default_tenant", "value": "tenant-demo" }]\n}')

add("Lab — List performance profiles", "lab", "GET", "/v1/playground/performance/profiles", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    '{ "profiles": [{ "name": "payment-throughput", "targetPath": "/v1/payments", "method": "POST", "suggestedVUs": 20, "durationSec": 120 }] }')
add("Lab — List security test cases", "lab", "GET", "/v1/playground/security/test-cases", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    '[{ "id": "bola-party-read", "category": "BOLA", "expectedStatus": 403, "method": "GET", "path": "/v1/parties/party-other" }]')
add("Lab — List concurrency scenarios", "lab", "GET", "/v1/playground/concurrency/scenarios", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    '[{ "slug": "concurrent-transfer-race", "title": "Concurrent Transfer Race" }]')
add("Lab — Get race profile", "lab", "GET", "/v1/playground/concurrency/scenarios/concurrent-transfer-race/race-profile", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    '{ "slug": "concurrent-transfer-race", "service": "banking-api", "method": "POST", "path": "/v1/transfers", "concurrency": 5 }')
add("Lab — List failure simulations", "lab", "GET", "/v1/playground/failures", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    '[{ "slug": "banking-latency-spike", "title": "Banking API Latency Spike" }]')
add("Lab — Get failure simulation", "lab", "GET", "/v1/playground/failures/banking-latency-spike", "Basic (learner/learner)", {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
    '{ "slug": "banking-latency-spike", "faultType": "LATENCY", "pathPattern": "/v1/payments/**", "config": { "delayMs": 2000 } }')

# --- LAB SCENARIO SLUGS (remaining 5) ---
for slug in ["idempotency-replay", "fraud-velocity-alert", "webhook-retry-dlq", "concurrent-transfer-race", "bola-party-access"]:
    add(f"Lab — Get scenario {slug}", "lab", "GET", f"/v1/scenarios/{slug}", "Basic (learner/learner)",
        {"Authorization": "Basic bGVhcm5lcjpsZWFybmVy"}, None, "200 OK",
        SCENARIO_DETAILS[slug])

# --- NEGATIVE / EDGE SCENARIOS ---
add("Banking — Login invalid password", "banking", "POST", "/v1/auth/login", "None", {"Content-Type": "application/json"},
    '{\n  "tenantId": "tenant-demo",\n  "username": "customer",\n  "password": "wrong"\n}', "401 Unauthorized",
    '{ "title": "Unauthorized", "status": 401, "detail": "Invalid credentials", "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc" }')

add("Banking — Wrong tenant header", "banking", "GET", "/v1/accounts?partyId=party-customer-1", "Bearer JWT",
    {"Authorization": "Bearer {{jwt_token}}", "X-Tenant-Id": "wrong-tenant", "Content-Type": "application/json"}, None, "403 Forbidden",
    '{ "title": "Forbidden", "status": 403, "detail": "X-Tenant-Id does not match token tenant", "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc" }')

add("Banking — Idempotency conflict", "banking", "POST", "/v1/payments", "Bearer JWT + Idempotency-Key",
    {**BEARER_CUSTOMER, "Idempotency-Key": "fixed-key-123"},
    '{\n  "partyId": "party-customer-1",\n  "accountId": "acct-customer-1",\n  "beneficiaryId": "ben-1",\n  "amount": 99.00,\n  "currency": "GBP",\n  "reference": "conflict"\n}', "409 Conflict",
    '{ "title": "Conflict", "status": 409, "detail": "Idempotency-Key was already used with a different request body", "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc" }',
    "Send after successful payment with same key but different amount.")

add("Banking — Insufficient funds", "banking", "POST", "/v1/payments", "Bearer JWT + Idempotency-Key",
    {**BEARER_CUSTOMER, **IDEM},
    '{\n  "partyId": "party-customer-1",\n  "accountId": "acct-customer-1",\n  "beneficiaryId": "ben-1",\n  "amount": 999999.00,\n  "currency": "GBP",\n  "reference": "too-much"\n}', "400 Bad Request",
    '{ "title": "Bad Request", "status": 400, "detail": "Insufficient funds", "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc" }')

add("Lab — Scenarios without auth", "lab", "GET", "/v1/scenarios", "None", {"Accept": "application/json"}, None, "401 Unauthorized",
    '{ "title": "Unauthorized", "status": 401, "correlationId": "c1d2e3f4-a5b6-7890-cdef-123456789abc" }')


out = Path(__file__).parent / "API-TEST-SCENARIOS.md"
text = []
text.append(f"# API Test Scenarios — Complete Reference ({len(scenarios)} scenarios)\n")
text.append("One test scenario per section. Copy each block into Postman.\n")
text.append("**Prerequisites:** `make up-full` running. Import `docs/postman/playground-environment.json` and `docs/postman/playground-api-collection.json`.\n")
text.append("**Postman variables:** `jwt_token`, `admin_jwt`, `idempotency_key`\n")
text.append("---\n")
text.append("## Scenario Index\n")
for s in scenarios:
    text.append(f"- **Scenario {s['num']:03d}** — {s['name']}")
text.append("\n---\n")
for s in scenarios:
    text.append(f"## Scenario {s['num']:03d} — {s['name']}\n")
    text.append(f"**URL:** `{s['url']}`  ")
    text.append(f"**Method:** `{s['method']}`  ")
    text.append(f"**Authentication:** {s['auth']}\n")
    text.append("### Headers\n")
    text.append("| Header | Value |")
    text.append("|--------|-------|")
    if s["headers"]:
        for k, v in s["headers"].items():
            text.append(f"| {k} | `{v}` |")
    else:
        text.append("| — | No headers required |")
    text.append("\n### Request Body\n")
    if s["body"]:
        text.append("```json")
        text.append(s["body"])
        text.append("```")
    else:
        text.append("None")
    text.append(f"\n### Expected Response\n")
    text.append(f"**Status Code:** `{s['status']}`\n")
    if s["response"] == "NO_BODY":
        text.append("**Response Body:** None (empty)")
    elif s["response"].startswith("("):
        text.append(s["response"])
    else:
        text.append("```json")
        text.append(s["response"])
        text.append("```")
    if s["notes"]:
        text.append(f"\n**Notes:** {s['notes']}")
    text.append("\n---\n")
out.write_text("\n".join(text))
print(f"Wrote {len(scenarios)} scenarios to {out}")

# --- Postman Collection v2.1 ---
MODULE_BASE_VAR = {
    "platform": "{{platform_base}}",
    "banking": "{{banking_base}}",
    "enterprise": "{{enterprise_base}}",
    "lab": "{{lab_base}}",
}
FOLDER_NAMES = {
    "platform": "01 — Platform API",
    "banking": "02 — Banking API",
    "enterprise": "03 — Enterprise API",
    "lab": "04 — Test Lab (Playground API)",
}
FOLDER_ORDER = ["platform", "banking", "enterprise", "lab"]


def expected_status_code(status_str: str) -> int:
    return int(status_str.split()[0])


def postman_headers(headers: dict | None) -> list[dict]:
    if not headers:
        return []
    out_headers = []
    for key, value in headers.items():
        v = value.replace("tenant-demo", "{{tenant_id}}")
        out_headers.append({"key": key, "value": v, "type": "text"})
    return out_headers


def postman_body(body: str | None) -> dict | None:
    if not body:
        return None
    raw = (
        body.replace("tenant-demo", "{{tenant_id}}")
        .replace('"username": "customer"', '"username": "{{customer_username}}"')
        .replace('"username": "ops"', '"username": "{{ops_username}}"')
        .replace('"username": "admin"', '"username": "{{admin_username}}"')
        .replace('"password": "password"', '"password": "{{customer_password}}"')
    )
    return {
        "mode": "raw",
        "raw": raw,
        "options": {"raw": {"language": "json"}},
    }


def postman_url(module: str, path: str) -> str:
    return MODULE_BASE_VAR[module] + path


def postman_description(s: dict) -> str:
    lines = [
        f"**Scenario {s['num']:03d}** — {s['name']}",
        "",
        f"**Authentication:** {s['auth']}",
        f"**Expected status:** `{s['status']}`",
        "",
    ]
    if s["notes"]:
        lines.extend([f"**Notes:** {s['notes']}", ""])
    if s["response"] == "NO_BODY":
        lines.append("**Expected response body:** None (empty)")
    elif not s["response"].startswith("("):
        lines.extend(["**Expected response:**", "```json", s["response"], "```"])
    return "\n".join(lines)


def postman_test_script(s: dict) -> list[str]:
    code = expected_status_code(s["status"])
    lines = [
        f"pm.test('Status code is {code}', function () {{",
        f"    pm.response.to.have.status({code});",
        "});",
    ]
    if s["response"] != "NO_BODY" and code not in (204,):
        lines.extend([
            "pm.test('Response is JSON when body expected', function () {",
            "    if (pm.response.code !== 204) {",
            "        pm.response.to.be.json;",
            "    }",
            "});",
        ])
    name = s["name"]
    if name == "Banking — Login as customer":
        lines.extend([
            "if (pm.response.code === 200) {",
            "    const json = pm.response.json();",
            "    pm.environment.set('jwt_token', json.accessToken);",
            "}",
        ])
    elif name in ("Banking — Login as ops", "Banking — Login as admin"):
        lines.extend([
            "if (pm.response.code === 200) {",
            "    const json = pm.response.json();",
            "    pm.environment.set('admin_jwt', json.accessToken);",
            "}",
        ])
    elif name == "Lab — Start scenario run":
        lines.extend([
            "if (pm.response.code === 201) {",
            "    const json = pm.response.json();",
            "    pm.environment.set('scenario_run_id', json.id);",
            "}",
        ])
    return lines


def postman_prerequest_script(s: dict) -> list[str] | None:
    headers = s.get("headers") or {}
    if "Idempotency-Key" in headers and headers["Idempotency-Key"] == "{{idempotency_key}}":
        return [
            "if (!pm.environment.get('idempotency_key')) {",
            "    pm.environment.set('idempotency_key', pm.variables.replaceIn('{{$guid}}'));",
            "}",
        ]
    return None


def build_postman_request(s: dict) -> dict:
    req_name = f"{s['num']:03d} — {s['name']}"
    request = {
        "method": s["method"],
        "header": postman_headers(s["headers"]),
        "url": postman_url(s["module"], s["path"]),
        "description": postman_description(s),
    }
    body = postman_body(s["body"])
    if body:
        request["body"] = body

    events = []
    test_lines = postman_test_script(s)
    if test_lines:
        events.append({
            "listen": "test",
            "script": {"type": "text/javascript", "exec": test_lines},
        })
    pre_lines = postman_prerequest_script(s)
    if pre_lines:
        events.append({
            "listen": "prerequest",
            "script": {"type": "text/javascript", "exec": pre_lines},
        })

    item = {"name": req_name, "request": request}
    if events:
        item["event"] = events
    return item


folders: dict[str, list] = {m: [] for m in FOLDER_ORDER}
for s in scenarios:
    folders[s["module"]].append(build_postman_request(s))

collection = {
    "info": {
        "_postman_id": str(uuid.uuid4()),
        "name": "API Testing Playground — 138 Scenarios",
        "description": (
            "Import with docs/postman/playground-environment.json.\n\n"
            "Run Scenario 008 first (customer login) to set jwt_token, "
            "then 009 or 010 for admin_jwt.\n\n"
            "Generated from docs/generate-api-test-scenarios.py — keep in sync with API-TEST-SCENARIOS.md."
        ),
        "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json",
    },
    "item": [
        {"name": FOLDER_NAMES[module], "item": folders[module]}
        for module in FOLDER_ORDER
        if folders[module]
    ],
}

collection_path = Path(__file__).parent / "postman" / "playground-api-collection.json"
collection_path.parent.mkdir(parents=True, exist_ok=True)
collection_path.write_text(json.dumps(collection, indent=2))
print(f"Wrote Postman collection ({len(scenarios)} requests) to {collection_path}")
