package com.playground.common.exception;

import org.springframework.http.HttpStatus;

public class PlaygroundException extends RuntimeException {

    private final HttpStatus status;
    private final String type;
    private final String title;

    public PlaygroundException(HttpStatus status, String type, String title, String detail) {
        super(detail);
        this.status = status;
        this.type = type;
        this.title = title;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }
}
