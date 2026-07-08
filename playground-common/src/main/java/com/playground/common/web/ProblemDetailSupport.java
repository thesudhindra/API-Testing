package com.playground.common.web;

import com.playground.common.context.CorrelationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;

public final class ProblemDetailSupport {

    private ProblemDetailSupport() {
    }

    public static ProblemDetail create(HttpStatus status, String type, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create(type));
        problem.setTitle(title);
        problem.setProperty("correlationId", CorrelationContext.getOrGenerate());
        return problem;
    }
}
