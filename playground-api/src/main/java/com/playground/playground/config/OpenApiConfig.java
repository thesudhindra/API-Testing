package com.playground.playground.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BASIC_AUTH_SCHEME = "basicAuth";

    @Bean
    public OpenAPI playgroundOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Testing Playground — Playground API")
                        .description("Learner lab: scenarios, faults, mocks, test data, contracts, and dashboard.")
                        .version("0.1.0")
                        .contact(new Contact().name("API Testing Playground")))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BASIC_AUTH_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("HTTP Basic credentials from playground.security.user/password")));
    }
}
