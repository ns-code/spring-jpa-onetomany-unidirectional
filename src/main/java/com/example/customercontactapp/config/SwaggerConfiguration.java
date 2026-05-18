package com.example.customercontactapp.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    // Option 1: Group API to show only UserController
    @Bean
    public GroupedOpenApi orderitemsApi() {
        return GroupedOpenApi.builder()
                .group("customercontacts-api")
                .displayName("Customers Contacts APIs")
                .pathsToMatch("/api/**")
                .build();
    }
}