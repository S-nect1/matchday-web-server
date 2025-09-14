package com.matchday.config.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme bearerAuthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("BearerAuth");

        return new OpenAPI()
                .info(new Info()
                        .title("MatchDay API")
                        .description("MatchDay 백엔드 API 문서")
                        .version("v1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", bearerAuthScheme))
                .addSecurityItem(securityRequirement);
    }

//    private GroupedOpenApi buildGroupedOpenApi(String group, String basePackage) {
//        return GroupedOpenApi.builder()
//                .group(group)
//                .pathsToMatch("/api/v1/**")
//                .packagesToScan(basePackage)
//                .build();
//    }
}