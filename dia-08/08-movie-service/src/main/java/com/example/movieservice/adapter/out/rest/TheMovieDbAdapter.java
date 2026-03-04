package com.example.movieservice.adapter.out.rest;

import com.example.movieservice.domain.model.Movie;
import com.example.movieservice.domain.model.MovieCredits;
import com.example.movieservice.domain.model.MoviePage;
import com.example.movieservice.domain.port.out.MovieApiPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// TODO 2: Implementar o Adapter REST TheMovieDbAdapter
//
// Esta classe:
//   - Implementa MovieApiPort (o port de saída do domínio)
//   - Usa o TheMovieDbClient (Feign) para fazer as chamadas HTTP
//   - Converte as respostas do TheMovieDB (TmdbXxxResponse) para objetos do domínio (Movie, MoviePage, MovieCredits)
//
// O que fazer:
//   1. Injetar TheMovieDbClient via construtor
//   2. Implementar os 4 métodos do MovieApiPort
//   3. Usar TmdbMovieMapper para converter TmdbXxxResponse → domínio
//   4. Sempre passar language="pt-BR" nas chamadas ao TheMovieDB
//
// TODO 6: Adicionar Resilience4j nesta classe:
//   - @Retry(name = "tmdbApi") nos métodos
//   - @CircuitBreaker(name = "tmdbApi", fallbackMethod = "xxxFallback") nos métodos
//   - Implementar métodos de fallback que retornam dados vazios ou cached
//
// TODO 7: Adicionar @Cacheable nos métodos de listagem:
//   - @Cacheable(value = "popular-movies", key = "#page") em getPopularMovies()
//   - TTL de 30 min já configurado no application.yml

@Component
public class TheMovieDbAdapter implements MovieApiPort /* TODO 2: implementar os 4 métodos */ {

    private static final Logger log = LoggerFactory.getLogger(TheMovieDbAdapter.class);
    private static final String LANGUAGE = "pt-BR";

    private final TheMovieDbClient client;

    public TheMovieDbAdapter(TheMovieDbClient client) {
        this.client = client;
    }

    // TODO 2: Implementar os métodos do MovieApiPort
    //
    // Exemplo de implementação do searchMovies:
    //
    // @Override
    // public MoviePage searchMovies(String query, int page) {
    //     log.info("Buscando filmes no TheMovieDB: query='{}', page={}", query, page);
    //     TmdbSearchResponse response = client.searchMovies(query, page, LANGUAGE);
    //     return TmdbMovieMapper.toMoviePage(response);
    // }

    // TODO 6: Exemplo de fallback para Resilience4j:
    //
    // public MoviePage searchMoviesFallback(String query, int page, Throwable t) {
    //     log.warn("Fallback ativado para searchMovies: {}", t.getMessage());
    //     return new MoviePage(List.of(), page, 0, 0);
    // }
}
