# Platform API Documentation

**Base URL:** `http://localhost:8080`  
**Authentication:** HTTP Basic (`learner` / `learner`) on protected routes  
**OpenAPI:** `http://localhost:8080/swagger-ui.html`

---

## Status

### Get API health

`GET /health`

Returns the status of the Platform API and its database dependency. **No authentication required.**

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| — | — | — | — | No parameters |

| Status code | Description |
|-------------|-------------|
| 200 OK | API and database are healthy |
| 503 Service Unavailable | Database or dependency unhealthy |

**Example response:**

```json
{
  "status": "UP",
  "service": "platform-api",
  "components": {
    "database": "UP"
  }
}
```

**What to test:** Response includes `X-Correlation-Id` header. `status` = `UP` when stack is running.

---

### Get API version

`GET /version`

Returns build and version metadata. **No authentication required.**

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| — | — | — | — | No parameters |

| Status code | Description |
|-------------|-------------|
| 200 OK | Version metadata returned |

**Example response:**

```json
{
  "name": "platform-api",
  "version": "0.1.0-SNAPSHOT"
}
```

---

## Demo

Teaching endpoints for RFC 7807 error handling and validation. **All require HTTP Basic authentication.**

### Trigger bad request error

`GET /v1/demo/errors/bad-request`

Returns a `400 Bad Request` problem+json response for SDET practice.

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| Authorization | string | header | Yes | Basic credentials |

| Status code | Description |
|-------------|-------------|
| 200 OK | — |
| 400 Bad Request | Demonstration error response |
| 401 Unauthorized | Missing or invalid Basic auth |

**Example response (400):**

```json
{
  "type": "https://playground.example/problems/bad-request",
  "title": "Bad Request",
  "status": 400,
  "detail": "Demonstration bad request",
  "correlationId": "abc-123"
}
```

---

### Trigger not found error

`GET /v1/demo/errors/not-found`

Returns a `404 Not Found` problem+json response.

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| Authorization | string | header | Yes | Basic credentials |

| Status code | Description |
|-------------|-------------|
| 404 Not Found | Demonstration not-found error |
| 401 Unauthorized | Missing auth |

---

### Trigger internal server error

`GET /v1/demo/errors/internal`

Returns a `500 Internal Server Error` problem+json response.

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| Authorization | string | header | Yes | Basic credentials |

| Status code | Description |
|-------------|-------------|
| 500 Internal Server Error | Demonstration server error |
| 401 Unauthorized | Missing auth |

---

### Validate request body

`POST /v1/demo/validate`

Demonstrates Jakarta Bean Validation → `422 Unprocessable Entity` with field-level errors.

The request body must be in JSON format.

| Name | Type | In | Required | Description |
|------|------|----|----------|-------------|
| Authorization | string | header | Yes | Basic credentials |
| name | string | body | Yes | Must be 2–50 characters, not blank |

**Example request body (valid):**

```json
{
  "name": "Jane"
}
```

**Example response (200):**

```json
{
  "message": "Validation passed",
  "name": "Jane"
}
```

**Example request body (invalid):**

```json
{
  "name": ""
}
```

| Status code | Description |
|-------------|-------------|
| 200 OK | Validation passed |
| 401 Unauthorized | Missing auth |
| 422 Unprocessable Entity | Validation failed — `errors[]` array in body |

**Example response (422):**

```json
{
  "type": "https://playground.example/problems/validation-failed",
  "title": "Validation Failed",
  "status": 422,
  "detail": "Request validation failed",
  "correlationId": "abc-123",
  "errors": [
    { "field": "name", "message": "name must not be blank" }
  ]
}
```

**What to test:**
- Call without auth → `401`
- Valid name → `200`
- Blank name → `422` with `errors` array
- Assert `correlationId` on all error responses

---

[← Back to index](README.md) · [Banking API →](banking-api.md)
