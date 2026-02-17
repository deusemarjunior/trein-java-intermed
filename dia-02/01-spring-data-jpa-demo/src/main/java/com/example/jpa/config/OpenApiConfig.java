package com.example.jpa.config;

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
                        .title("Spring Data JPA Demo API")
                        .version("1.0.0")
                        .description("""
                                API demonstrativa de Spring Data JPA - Dia 02.
                                
                                Demonstra os principais recursos do Spring Data JPA:
                                - Repositories e Query Methods
                                - Paginação e Ordenação
                                - Queries customizadas (JPQL e Native)
                                - Projeções (Projections)
                                - Specification para buscas dinâmicas
                                - Relacionamentos JPA (OneToMany, ManyToMany)
                                """)
                        .contact(new Contact()
                                .name("Treinamento Java Intermediário")
                                .url("https://github.com/trein-java-intermed")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor Local")));
    }
}
