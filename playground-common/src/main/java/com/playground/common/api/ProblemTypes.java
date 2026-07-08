package com.playground.common.api;

public final class ProblemTypes {

    public static final String BASE = "https://playground.local/problems/";
    public static final String VALIDATION = BASE + "validation-error";
    public static final String NOT_FOUND = BASE + "not-found";
    public static final String BAD_REQUEST = BASE + "bad-request";
    public static final String CONFLICT = BASE + "conflict";
    public static final String INTERNAL = BASE + "internal-error";
    public static final String UNAUTHORIZED = BASE + "unauthorized";
    public static final String FORBIDDEN = BASE + "forbidden";

    private ProblemTypes() {
    }
}
