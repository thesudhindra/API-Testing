package com.playground.common.autoconfigure;

import com.playground.common.web.CorrelationIdFilter;
import com.playground.common.web.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnWebApplication
@Import(GlobalExceptionHandler.class)
public class PlaygroundCommonAutoConfiguration {

    @Bean
    CorrelationIdFilter correlationIdFilter() {
        return new CorrelationIdFilter();
    }
}
