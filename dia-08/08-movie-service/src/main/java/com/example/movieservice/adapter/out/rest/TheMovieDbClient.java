package com.example.movieservice.adapter.out.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

// TODO 2: Implementar o Feign Client para consumir a API do TheMovieDB
//
// Este client é a interface DECLARATIVA que o Feign usa para fazer requisições HTTP.
// A URL base vem do application.yml: tmdb.api.base-url
//
// Endpoints do TheMovieDB que devem ser mapeados:
//
//   GET /search/movie?query={query}&page={page}&language=pt-BR
//       → Retorna TmdbSearchResponse (lista paginada de filmes)
//
//   GET /movie/{movieId}?language=pt-BR
//       → Retorna TmdbMovieResponse (detalhes de um filme)
//
//   GET /movie/popular?page={page}&language=pt-BR
//       → Retorna TmdbSearchResponse (filmes populares paginados)
//
//   GET /movie/{movieId}/credits
//       → Retorna TmdbCreditsResponse (elenco e equipe)
//
// Autenticação:
//   O TheMovieDB usa Bearer Token no header Authorization.
//   Configure no FeignConfig.java um RequestInterceptor que adiciona:
//   Authorization: Bearer {tmdb.api.key}
//
// Dica: Use a anotação @FeignClient com:
//   name = "tmdb-client"
//   url = "${tmdb.api.base-url}"
//   configuration = FeignConfig.class

@FeignClient(
        name = "tmdb-client",
        url = "${tmdb.api.base-url}",
        configuration = FeignConfig.class
)
public interface TheMovieDbClient {

    // TODO 2: Mapear os 4 endpoints do TheMovieDB aqui
    //
    // Exemplo:
    //
    // @GetMapping("/search/movie")
    // TmdbSearchResponse searchMovies(
    //         @RequestParam("query") String query,
    //         @RequestParam("page") int page,
    //         @RequestParam("language") String language
    // );
    //
    // @GetMapping("/movie/{movieId}")
    // TmdbMovieResponse getMovieDetails(
    //         @PathVariable("movieId") Long movieId,
    //         @RequestParam("language") String language
    // );
    //
    // @GetMapping("/movie/popular")
    // TmdbSearchResponse getPopularMovies(
    //         @RequestParam("page") int page,
    //         @RequestParam("language") String language
    // );
    //
    // @GetMapping("/movie/{movieId}/credits")
    // TmdbCreditsResponse getMovieCredits(@PathVariable("movieId") Long movieId);

}
