package com.pushnotification.pushnotification.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;


import org.springframework.context.annotation.Configuration;
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "FCM Push Notification",
                version = "1.0",
                description = "This is FCM Push Notification documentation",
                contact = @io.swagger.v3.oas.annotations.info.Contact(
                        name = "Murad Guliyev",
                        email = "quliyev.murad@yahoo.com",
                        url = ""
                )
        )
)
public class SwaggerConfig {
//    @Bean
//    public GroupedOpenApi publicApi() {
//        return GroupedOpenApi.builder()
//                .group("springdoc-openapi")
//                .pathsToMatch("/**")
//                .build();
//    }
}