package com.ppp.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerConfig {
    @Value(value = "${project.version}")
    private String version;

    @Bean
    public GroupedOpenApi groupedOpenApi(){
        return GroupedOpenApi.builder()
                .group(version)
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("My PetLog Server API")
                .description("My PetLog api 명세서");

        // Security 스키마 설정
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("AccessToken")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        // Security 요청 설정
        SecurityRequirement addSecurityItem = new SecurityRequirement();
        addSecurityItem.addList("Authorization");

        // 경로 설정
        Paths paths = new Paths();
        // OpenAPI 설정
        return new OpenAPI()
                .info(info)
                // Security 인증 컴포넌트 설정
                .components(new Components().addSecuritySchemes("Authorization", bearerAuth))
                // API 마다 Security 인증 컴포넌트 설정
                .addSecurityItem(addSecurityItem)
                .paths(paths);
    }

}
