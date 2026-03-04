package com.example.blog.config;

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
                        .title("Blog API")
                        .version("1.0.0")
                        .description("""
                                API de Blog - Exercício de Relacionamentos JPA.
                                
                                Demonstra conceitos de:
                                - Relacionamentos OneToMany (Post → Comments)
                                - Relacionamentos ManyToMany (Post ↔ Tags)
                                - Cascade e Orphan Removal
                                - JOIN FETCH para resolver N+1
                                - Query Methods personalizados
                                """)
                        .contact(new Contact()
                                .name("Treinamento Java Intermediário")
                                .url("https://github.com/trein-java-intermed")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Servidor Local")));
    }
}
