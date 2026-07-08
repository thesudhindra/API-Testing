package com.playground.common.exception;

import com.playground.common.api.ProblemTypes;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends PlaygroundException {

    public ResourceNotFoundException(String detail) {
        super(HttpStatus.NOT_FOUND, ProblemTypes.NOT_FOUND, "Not Found", detail);
    }
}
