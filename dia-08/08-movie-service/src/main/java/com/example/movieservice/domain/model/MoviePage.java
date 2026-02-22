package com.example.movieservice.domain.model;

import java.util.List;

/**
 * Modelo de domínio representando uma página de resultados de filmes.
 * Usado tanto para busca quanto para listagem de populares.
 */
public record MoviePage(
        List<Movie> movies,
        int page,
        int totalPages,
        long totalResults
) {
}
