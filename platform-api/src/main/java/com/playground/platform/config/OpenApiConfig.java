package com.playground.platform.config;

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
    public OpenAPI platformOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Testing Playground — Platform API")
                        .description("Foundation platform service (Phase 1). Health, version, and RFC 7807 error teaching endpoints.")
                        .version("0.1.0")
                        .contact(new Contact().name("API Testing Playground")))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BASIC_AUTH_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Foundation-phase HTTP Basic (replaced by OAuth2 in later milestones)")));
    }
}
