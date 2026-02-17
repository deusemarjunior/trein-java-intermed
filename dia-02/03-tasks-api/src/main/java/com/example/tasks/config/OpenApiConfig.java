package com.example.tasks.config;

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
                        .title("Tasks API")
                        .version("1.0.0")
                        .description("""
                                API de Tarefas - Exercício de Migração para Persistência.
                                
                                Demonstra conceitos de:
                                - CRUD completo com Spring Data JPA
                                - Paginação e Ordenação
                                - Busca avançada com filtros dinâmicos
                                - Validação com Bean Validation
                                - DTOs com Records
                                - Tratamento de exceções
                                """)
                        .contact(new Contact()
                                .name("Treinamento Java Intermediário")
                                .url("https://github.com/trein-java-intermed")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Servidor Local")));
    }
}
