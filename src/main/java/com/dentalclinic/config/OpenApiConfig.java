package com.dentalclinic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 & Swagger UI Configuration Class.
 * 
 * Layer: Configuration Layer
 * Purpose: Exposes interactive REST API documentation at /swagger-ui.html and /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sunrise Dental Clinic Management System API")
                        .version("1.0.0")
                        .description("RESTful Web Services API for patient registration, dentist schedules, dynamic slot booking, billing, and report analytics.")
                        .contact(new Contact()
                                .name("Sunrise Dental Clinic Support")
                                .email("support@sunrisedental.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
