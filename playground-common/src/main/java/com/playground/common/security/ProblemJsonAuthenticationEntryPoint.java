package com.playground.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.common.api.ProblemTypes;
import com.playground.common.web.ProblemDetailSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class ProblemJsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public ProblemJsonAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        ProblemDetail problem = ProblemDetailSupport.create(
                HttpStatus.UNAUTHORIZED,
                ProblemTypes.UNAUTHORIZED,
                "Unauthorized",
                "Authentication is required");
        problem.setInstance(java.net.URI.create(request.getRequestURI()));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
