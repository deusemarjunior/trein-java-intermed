package com.example.cleanarchitecture.config;

import com.example.cleanarchitecture.domain.port.in.ProductUseCase;
import com.example.cleanarchitecture.domain.port.out.ProductRepositoryPort;
import com.example.cleanarchitecture.domain.service.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de beans — conecta os ports com as implementações.
 * É aqui que "ligamos" o domínio aos adapters.
 */
@Configuration
public class BeanConfig {

    @Bean
    public ProductUseCase productUseCase(ProductRepositoryPort repositoryPort) {
        return new ProductService(repositoryPort);
    }
}
