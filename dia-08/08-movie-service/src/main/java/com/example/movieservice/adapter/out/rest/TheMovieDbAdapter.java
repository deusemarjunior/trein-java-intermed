package com.example.movieservice.adapter.out.rest;

import com.example.movieservice.adapter.out.rest.dto.TmdbCreditsResponse;
import com.example.movieservice.adapter.out.rest.dto.TmdbMovieMapper;
import com.example.movieservice.adapter.out.rest.dto.TmdbMovieResponse;
import com.example.movieservice.adapter.out.rest.dto.TmdbSearchResponse;
import com.example.movieservice.domain.model.Movie;
import com.example.movieservice.domain.model.MovieCredits;
import com.example.movieservice.domain.model.MoviePage;
import com.example.movieservice.domain.port.out.MovieApiPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
public class TheMovieDbAdapter implements MovieApiPort  {

    private static final Logger log = LoggerFactory.getLogger(TheMovieDbAdapter.class);
    private static final String LANGUAGE = "pt-BR";

    private final TheMovieDbClient client;

    public TheMovieDbAdapter(TheMovieDbClient client) {
        this.client = client;
    }

    @Override
    public MoviePage searchMovies(String query, int page) {
        log.info("Buscando filmes no TheMovieDB: query='{}', page={}", query, page);
        TmdbSearchResponse response = client.searchMovies(query, page, LANGUAGE);
        return TmdbMovieMapper.toMoviePage(response);
    }

    @Override
    public Movie getMovieDetails(Long movieId){
        log.info("Buscando detalhes do filme no TheMovieDB: movieId={}", movieId);
        TmdbMovieResponse response = client.getMovieDetails(movieId, LANGUAGE);
        return TmdbMovieMapper.toMovie(response);
    }

    @Override
    public MoviePage getPopularMovies(int page) {
        log.info("Buscando filmes populares no TheMovieDB: page={}", page);
        TmdbSearchResponse response = client.getPopularMovies(page, LANGUAGE);
        return TmdbMovieMapper.toMoviePage(response);
    }

    @Override
    public MovieCredits getMovieCredits(Long movieId) {
        log.info("Buscando créditos do filme no TheMovieDB: movieId={}", movieId);
        TmdbCreditsResponse response = client.getMovieCredits(movieId);
        return TmdbMovieMapper.toMovieCredits(response);
    }

    // TODO 6: Exemplo de fallback para Resilience4j:
    //
    // public MoviePage searchMoviesFallback(String query, int page, Throwable t) {
    // log.warn("Fallback ativado para searchMovies: {}", t.getMessage());
    // return new MoviePage(List.of(), page, 0, 0);
    // }
}