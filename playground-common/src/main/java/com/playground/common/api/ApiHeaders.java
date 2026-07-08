package com.playground.common.api;

public final class ApiHeaders {

    public static final String CORRELATION_ID = "X-Correlation-Id";
    public static final String TENANT_ID = "X-Tenant-Id";
    public static final String IDEMPOTENCY_KEY = "Idempotency-Key";

    private ApiHeaders() {
    }
}
