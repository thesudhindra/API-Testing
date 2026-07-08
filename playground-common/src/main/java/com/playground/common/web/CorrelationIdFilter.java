package com.playground.common.web;

import com.playground.common.api.ApiHeaders;
import com.playground.common.context.CorrelationContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String correlationId = resolveCorrelationId(request);
        CorrelationContext.set(correlationId);
        MDC.put(MDC_KEY, correlationId);
        response.setHeader(ApiHeaders.CORRELATION_ID, correlationId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
            CorrelationContext.clear();
        }
    }

    private String resolveCorrelationId(HttpServletRequest request) {
        String header = request.getHeader(ApiHeaders.CORRELATION_ID);
        if (StringUtils.hasText(header)) {
            return header.trim();
        }
        return UUID.randomUUID().toString();
    }
}
