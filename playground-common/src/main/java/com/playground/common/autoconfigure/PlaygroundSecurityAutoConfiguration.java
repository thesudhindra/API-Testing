package com.playground.common.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.common.security.ProblemJsonAccessDeniedHandler;
import com.playground.common.security.ProblemJsonAuthenticationEntryPoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.security.web.SecurityFilterChain")
public class PlaygroundSecurityAutoConfiguration {

    @Bean
    ProblemJsonAuthenticationEntryPoint problemJsonAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new ProblemJsonAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    ProblemJsonAccessDeniedHandler problemJsonAccessDeniedHandler(ObjectMapper objectMapper) {
        return new ProblemJsonAccessDeniedHandler(objectMapper);
    }
}
