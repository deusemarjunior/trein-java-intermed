package com.example.movieservice.adapter.out.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO que mapeia a resposta de busca/popular do TheMovieDB.
 * Formato: GET /search/movie e GET /movie/popular
 *
 * Já implementado — o aluno NÃO precisa alterar este arquivo.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbSearchResponse(

        int page,

        List<TmdbMovieResult> results,

        @JsonProperty("total_pages")
        int totalPages,

        @JsonProperty("total_results")
        long totalResults

) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmdbMovieResult(

            Long id,

            String title,

            String overview,

            @JsonProperty("poster_path")
            String posterPath,

            @JsonProperty("backdrop_path")
            String backdropPath,

            @JsonProperty("release_date")
            String releaseDate,

            @JsonProperty("vote_average")
            Double voteAverage,

            @JsonProperty("vote_count")
            Integer voteCount,

            Double popularity
    ) {
    }
}
