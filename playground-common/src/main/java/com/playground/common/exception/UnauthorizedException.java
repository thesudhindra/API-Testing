package com.playground.common.exception;

import com.playground.common.api.ProblemTypes;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends PlaygroundException {

    public UnauthorizedException(String detail) {
        super(HttpStatus.UNAUTHORIZED, ProblemTypes.UNAUTHORIZED, "Unauthorized", detail);
    }
}
