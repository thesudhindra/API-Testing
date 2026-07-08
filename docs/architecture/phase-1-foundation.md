# Phase 1 — Foundation

## Scope

Phase 1 implements milestone **M01–M02** infrastructure:

| Deliverable | Module / path |
|-------------|----------------|
| Maven parent BOM | `/pom.xml` |
| Common library | `playground-common/` |
| Platform service | `platform-api/` |
| PostgreSQL | `docker-compose.yml` |
| Flyway baseline | `platform-api/src/main/resources/db/migration/` |
| OpenAPI / Swagger UI | springdoc + per-service `OpenApiConfig` |
| Spring Security base | HTTP Basic, public health/version/docs |
| Global exception handling | `playground-common` → RFC 7807 problem+json |
| Structured logging | Logback pattern with `correlationId` MDC |
| Validation | Jakarta Validation on demo endpoint |
| Health API | `GET /health` |
| Version API | `GET /version` |
| CI | `.github/workflows/ci.yml` |

**Phase 4** adds `enterprise-api` (see [phase-4-enterprise-features.md](phase-4-enterprise-features.md)).

**Phase 5** adds `playground-api` — the API Testing Playground / Test Lab (see [phase-5-api-testing-playground.md](phase-5-api-testing-playground.md)).

## Modules

### `playground-common`

Reusable across services:

- `ApiHeaders` — standard header names (`X-Correlation-Id`, `X-Tenant-Id`, `Idempotency-Key`)
- `PageResponse` — shared pagination envelope
- `TenantAccess` / `JwtSupport` — JWT tenant validation and role checks (banking-api, enterprise-api)
- `DigestSupport` — SHA-256 helpers for idempotency and audit hash chains
- `ProblemTypes` — stable problem `type` URIs
- `CorrelationIdFilter` + `CorrelationContext`
- `GlobalExceptionHandler` — RFC 7807 responses (including nested security advice when Spring Security is present)
- `ProblemJsonAuthenticationEntryPoint` / `ProblemJsonAccessDeniedHandler` — RFC 7807 for 401/403 from security filters
- `HealthSupport` / `VersionSupport` — shared bootstrap response builders
- Domain exceptions: `BadRequestException`, `ConflictException`, `ForbiddenException`, `UnauthorizedException`, `ResourceNotFoundException`
- `PlaygroundCommonAutoConfiguration` + `PlaygroundSecurityAutoConfiguration` — auto-import via Spring Boot 3

### `platform-api`

Runnable Spring Boot 3.3 application on port 8080.

## Local development

```bash
# Terminal 1 — database only
docker compose up postgres -d

# Terminal 2 — run API from IDE or:
mvn -pl platform-api spring-boot:run
```

Environment variables: see `.env.example`.

## Docker profiles

| Profile | Services |
|---------|----------|
| (default) | `postgres` only |
| `foundation` | `postgres` + `platform-api` |
| `core` | `postgres` + `platform-api` + `banking-api` |
| `full` | `postgres` + `platform-api` + `banking-api` + `enterprise-api` + `playground-api` |

## Next milestones

- **M06:** API Gateway in front of services
- **M07+:** Keycloak JWT (replacing symmetric JWT in banking-api)
- **Gateway fault injection:** Wire playground fault rules to live traffic
