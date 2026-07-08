package com.playground.common.exception;

import com.playground.common.api.ProblemTypes;
import org.springframework.http.HttpStatus;

public class BadRequestException extends PlaygroundException {

    public BadRequestException(String detail) {
        super(HttpStatus.BAD_REQUEST, ProblemTypes.BAD_REQUEST, "Bad Request", detail);
    }
}
