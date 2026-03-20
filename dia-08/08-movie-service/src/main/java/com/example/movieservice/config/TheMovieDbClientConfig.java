package com.example.movieservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class TheMovieDbClientConfig {

    @Value("${themoviedb.api.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor authInterceptor() {
        return template -> template.header(
            "Authorization", "Bearer " + apiKey
        );
    }
}