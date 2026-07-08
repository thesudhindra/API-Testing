# Phase 2 — Core Banking

Phase 2 adds the **banking-api** service (port 8081) with core retail banking domains on top of the Phase 1 foundation.

## Scope

| Domain | Module prefix | Notes |
|--------|---------------|-------|
| Authentication | `/v1/auth` | JWT login (`POST /login`), profile (`GET /me`) |
| Users | `/v1/users` | Tenant-scoped user admin (ADMIN create, OPS list) |
| Roles | `/v1/roles` | Tenant role catalog |
| Customer (Party) | `/v1/parties` | Individual/corporate party lifecycle |
| KYC | `/v1/kyc-cases` | Submit and review cases |
| Accounts | `/v1/accounts` | Open/list accounts (requires approved KYC) |
| Beneficiaries | `/v1/beneficiaries` | Saved payees (soft delete) |

**Out of scope for Phase 2:** Payments, Cards, Loans, Notifications.

## Architecture

```
┌─────────────────┐     ┌─────────────────┐
│  platform-api   │     │   banking-api   │
│     :8080       │     │     :8081       │
│  schema: public │     │ schema: banking │
└────────┬────────┘     └────────┬────────┘
         │                       │
         └───────────┬───────────┘
                     ▼
              PostgreSQL (playground)
```

- **Shared library:** `playground-common` (RFC 7807 errors, correlation ID, shared health/version helpers)
- **Auth:** Symmetric HS256 JWT (Keycloak planned for later milestones)
- **Multi-tenancy:** `tenant_id` on tenant-owned rows; `X-Tenant-Id` header validated against JWT `tenant_id` claim in `TenantAccess` (`playground-common`)
- **Authorization:** Role-based (`RETAIL_CUSTOMER`, `OPS_AGENT`, `ADMIN`) with party-level BOLA checks for customers

## Database

Flyway migrations live in `banking-api/src/main/resources/db/migration/` under schema `banking`:

- `V1__banking_core_schema.sql` — tables (`kyc_cases` is scoped to tenant indirectly via `party_id` → `parties.tenant_id`)
- `V2__banking_seed_data.sql` — demo tenant and users

### Seed credentials

| Tenant | Username | Password | Role |
|--------|----------|----------|------|
| `tenant-demo` | `customer` | `password` | RETAIL_CUSTOMER (linked to `party-customer-1`, KYC APPROVED) |
| `tenant-demo` | `ops` | `password` | OPS_AGENT |
| `tenant-demo` | `admin` | `password` | ADMIN |

## API quick start

```bash
# Login
curl -s -X POST http://localhost:8081/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"tenantId":"tenant-demo","username":"customer","password":"password"}' | jq

# Use token
TOKEN=... # from accessToken
curl -s http://localhost:8081/v1/auth/me \
  -H "Authorization: Bearer $TOKEN" \
  -H 'X-Tenant-Id: tenant-demo' | jq
```

OpenAPI UI: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

## Docker

```bash
make up-core   # postgres + platform-api + banking-api
```

## Testing dimensions (learning hooks)

Phase 2 endpoints are designed for exercises in auth (JWT expiry, invalid signature), validation (422), RBAC (403), BOLA (cross-party access), idempotency patterns (future), and pagination.
