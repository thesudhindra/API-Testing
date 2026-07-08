# API Documentation

## Primary SDET / Postman reference (138 scenarios)

### → [API-TEST-SCENARIOS.md](../API-TEST-SCENARIOS.md)

**One numbered scenario per test case.** Each scenario includes:

- Complete URL (e.g. `http://localhost:8081/v1/payments`)
- HTTP method (`GET`, `POST`, `PATCH`, `PUT`, `DELETE`)
- Authentication type
- Full headers table
- Request body JSON (or `None`)
- Expected status code
- Expected response JSON (or explicit empty body)

**Setup:** `make up-full` · Import Postman files:

- **Environment:** [postman/playground-environment.json](postman/playground-environment.json)
- **Collection (138 requests):** [postman/playground-api-collection.json](postman/playground-api-collection.json)

In Postman: **Import** → select both files → choose environment **API Testing Playground — Local** → run **008 — Banking — Login as customer** first.

**Run order:** Scenario 008 (customer login) → save `jwt_token` → Scenario 009/010 for `admin_jwt` → remaining scenarios.

---

## Other references

| Document | Purpose |
|----------|---------|
| [API-TESTING-GUIDE.md](../API-TESTING-GUIDE.md) | Linear endpoint catalog (grocery-store style) |
| [sdet-postman-testing-guide.md](../sdet-postman-testing-guide.md) | Workflows, negative tests, lab exercises |
| `platform-api.md`, `banking-api.md`, etc. | Optional per-module split copies |
