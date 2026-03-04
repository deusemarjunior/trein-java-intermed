package com.example.products.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Products API")
                        .version("1.0.0")
                        .description("""
                                Products REST API - Spring Boot Demo.
                                
                                API completa de produtos demonstrando:
                                - CRUD com Spring Data JPA
                                - Validação com Bean Validation
                                - DTOs com Records (Request/Response)
                                - Tratamento global de exceções
                                - Profiles (dev/prod)
                                """)
                        .contact(new Contact()
                                .name("Treinamento Java Intermediário")
                                .url("https://github.com/trein-java-intermed")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor Local")));
    }
}
