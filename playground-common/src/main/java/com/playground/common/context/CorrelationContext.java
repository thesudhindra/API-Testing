package com.playground.common.context;

import java.util.Optional;
import java.util.UUID;

public final class CorrelationContext {

    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();

    private CorrelationContext() {
    }

    public static void set(String correlationId) {
        CORRELATION_ID.set(correlationId);
    }

    public static Optional<String> get() {
        return Optional.ofNullable(CORRELATION_ID.get());
    }

    public static String getOrGenerate() {
        return get().orElseGet(() -> UUID.randomUUID().toString());
    }

    public static void clear() {
        CORRELATION_ID.remove();
    }
}
