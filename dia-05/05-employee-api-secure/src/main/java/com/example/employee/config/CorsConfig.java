package com.example.employee.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * TODO 4: Configurar CORS para a API.
 *
 * Implemente o método addCorsMappings:
 * 1. Mapear "/api/**"
 * 2. Permitir origens: http://localhost:3000, http://localhost:5173
 * 3. Métodos: GET, POST, PUT, DELETE, OPTIONS
 * 4. Headers: todos (*)
 * 5. Expor header "Authorization"
 * 6. allowCredentials(true)
 * 7. maxAge(3600)
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // TODO 4: Implementar configuração CORS
        //
        // registry.addMapping("/api/**")
        //         .allowedOrigins("http://localhost:3000", "http://localhost:5173")
        //         .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        //         .allowedHeaders("*")
        //         .exposedHeaders("Authorization")
        //         .allowCredentials(true)
        //         .maxAge(3600);
    }
}
