package com.example.products.config;

import com.example.products.model.Product;
import com.example.products.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

/**
 * Configuração da aplicação
 */
@Configuration
public class AppConfig {
    
    /**
     * Carregar dados de teste apenas em ambiente dev
     */
    @Bean
    @Profile("dev")
    public CommandLineRunner loadTestData(ProductRepository repository) {
        return args -> {
            System.out.println("\n🔄 Loading test data...");
            
            repository.save(new Product(
                "Laptop Gaming", 
                "High-end gaming laptop with RTX 4080", 
                BigDecimal.valueOf(7500.00), 
                "Electronics"
            ));
            
            repository.save(new Product(
                "Mouse Gamer", 
                "RGB gaming mouse with 16000 DPI", 
                BigDecimal.valueOf(250.00), 
                "Electronics"
            ));
            
            repository.save(new Product(
                "Teclado Mecânico", 
                "Mechanical keyboard with Cherry MX switches", 
                BigDecimal.valueOf(450.00), 
                "Electronics"
            ));
            
            repository.save(new Product(
                "Monitor 4K", 
                "27-inch 4K UHD monitor", 
                BigDecimal.valueOf(1800.00), 
                "Electronics"
            ));
            
            repository.save(new Product(
                "Cadeira Gamer", 
                "Ergonomic gaming chair", 
                BigDecimal.valueOf(1200.00), 
                "Furniture"
            ));
            
            repository.save(new Product(
                "Mesa de Escritório", 
                "Height-adjustable desk", 
                BigDecimal.valueOf(900.00), 
                "Furniture"
            ));
            
            long count = repository.count();
            System.out.println("✅ Loaded " + count + " test products\n");
        };
    }
}
