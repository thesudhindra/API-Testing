package com.playground.common.exception;

import com.playground.common.api.ProblemTypes;
import org.springframework.http.HttpStatus;

public class ConflictException extends PlaygroundException {

    public ConflictException(String detail) {
        super(HttpStatus.CONFLICT, ProblemTypes.CONFLICT, "Conflict", detail);
    }
}
