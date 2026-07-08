package com.playground.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.common.api.ProblemTypes;
import com.playground.common.web.ProblemDetailSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class ProblemJsonAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public ProblemJsonAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        ProblemDetail problem = ProblemDetailSupport.create(
                HttpStatus.FORBIDDEN,
                ProblemTypes.FORBIDDEN,
                "Forbidden",
                accessDeniedException.getMessage() == null ? "Access is denied" : accessDeniedException.getMessage());
        problem.setInstance(java.net.URI.create(request.getRequestURI()));
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
