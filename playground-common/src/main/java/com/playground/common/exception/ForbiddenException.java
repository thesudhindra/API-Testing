package com.playground.common.exception;

import com.playground.common.api.ProblemTypes;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends PlaygroundException {

    public ForbiddenException(String detail) {
        super(HttpStatus.FORBIDDEN, ProblemTypes.FORBIDDEN, "Forbidden", detail);
    }
}
