package com.example.movieservice.adapter.out.rest;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Feign Client para o TheMovieDB.
 * Adiciona o Bearer Token em todas as requisições automaticamente.
 * Já implementado — o aluno NÃO precisa alterar este arquivo.
 */
@Configuration
public class FeignConfig {

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor tmdbAuthInterceptor() {
        return (RequestTemplate template) -> {
            template.header("Authorization", "Bearer " + apiKey);
            template.header("Accept", "application/json");
        };
    }
}
