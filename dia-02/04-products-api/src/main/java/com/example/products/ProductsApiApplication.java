package com.example.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Spring Boot
 * 
 * @SpringBootApplication combina:
 * - @Configuration: Classe de configuração
 * - @EnableAutoConfiguration: Auto-configuração do Spring Boot
 * - @ComponentScan: Escaneia componentes no pacote e subpacotes
 */
@SpringBootApplication
public class ProductsApiApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProductsApiApplication.class, args);
        System.out.println("""
            
            ═══════════════════════════════════════════════════
            ✅ Products API Started Successfully!
            
            📋 Endpoints disponíveis:
               GET    /api/products       - Listar todos
               GET    /api/products/{id}  - Buscar por ID
               POST   /api/products       - Criar produto
               PUT    /api/products/{id}  - Atualizar produto
               DELETE /api/products/{id}  - Deletar produto
            
            🗄️  H2 Console: http://localhost:8080/h2-console
               JDBC URL: jdbc:h2:mem:testdb
               User: sa
               Password: (vazio)
            
            📖 API: http://localhost:8080/api/products
            ═══════════════════════════════════════════════════
            """);
    }
}
