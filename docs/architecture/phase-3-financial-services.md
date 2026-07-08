# Phase 3 — Financial Services

Phase 3 extends **banking-api** with money movement, ledger, cards, FX, statements, and transaction history. Shared pagination and tenancy helpers live in `playground-common`.

## Scope

| Domain | Base path | Notes |
|--------|-----------|-------|
| Payments | `/v1/payments` | Outbound payments to beneficiaries |
| Transfers | `/v1/transfers` | Internal account-to-account transfers |
| Ledger | `/v1/ledger-entries` | Posted debit/credit entries |
| Cards | `/v1/cards` | Issue cards and authorize purchases |
| FX | `/v1/fx` | Quotes and conversions |
| Statements | `/v1/accounts/{id}/statements`, `/v1/statements/{id}` | Period statements with lines |
| Transactions | `/v1/transactions` | Unified financial transaction history |

**Out of scope for Phase 3:** Fraud, AML, Reporting, Background Jobs.

## Cross-cutting patterns

| Pattern | Implementation |
|---------|----------------|
| Transactions | `financial_transactions` hub; domain tables reference `transaction_id` |
| Idempotency | `Idempotency-Key` header on POST payments, transfers, FX conversions, card authorizations; stored in `idempotency_keys` (24h TTL) |
| Optimistic locking | `@Version` on `accounts` and `financial_transactions`; concurrent balance updates return 409 |
| Audit hooks | `AuditService.record()` inside the same DB transaction (`Propagation.MANDATORY`) |
| Pagination | `PageResponse` with `page` / `size` query params |
| Filtering | Transactions: `type`, `status`, `from`, `to`; Ledger: `from`, `to`; Payments: `status` |

## Database

Flyway migrations:

- `V3__financial_services_schema.sql` — audit, idempotency, transactions, ledger, payments, transfers, cards, FX, statements
- `V4__financial_services_seed.sql` — demo accounts (`acct-customer-1`, `acct-customer-2`, `acct-customer-eur`), beneficiary `ben-1`, card `card-1`

### Seed balances

| Account | Currency | Balance |
|---------|----------|---------|
| `acct-customer-1` | GBP | £5,000 |
| `acct-customer-2` | GBP | £1,000 |
| `acct-customer-eur` | EUR | €200 |

## API examples

```bash
TOKEN=$(curl -s -X POST http://localhost:8081/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"tenantId":"tenant-demo","username":"customer","password":"password"}' | jq -r .accessToken)

# Payment (idempotent)
curl -s -X POST http://localhost:8081/v1/payments \
  -H "Authorization: Bearer $TOKEN" \
  -H 'X-Tenant-Id: tenant-demo' \
  -H 'Idempotency-Key: demo-pay-1' \
  -H 'Content-Type: application/json' \
  -d '{
    "partyId":"party-customer-1",
    "accountId":"acct-customer-1",
    "beneficiaryId":"ben-1",
    "amount":10.00,
    "currency":"GBP",
    "reference":"demo"
  }' | jq

# Transfer between own accounts
curl -s -X POST http://localhost:8081/v1/transfers \
  -H "Authorization: Bearer $TOKEN" \
  -H 'X-Tenant-Id: tenant-demo' \
  -H 'Idempotency-Key: demo-xfer-1' \
  -H 'Content-Type: application/json' \
  -d '{
    "fromAccountId":"acct-customer-1",
    "toAccountId":"acct-customer-2",
    "amount":50.00,
    "currency":"GBP"
  }' | jq

# Transaction history with filters
curl -s "http://localhost:8081/v1/transactions?accountId=acct-customer-1&type=PAYMENT&page=0&size=20" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'X-Tenant-Id: tenant-demo' | jq
```

OpenAPI UI: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

## Testing dimensions

Phase 3 endpoints support exercises in idempotency (replay vs conflict), insufficient funds (422), optimistic locking (409), audit trail verification, pagination boundaries, and date-range filtering.
