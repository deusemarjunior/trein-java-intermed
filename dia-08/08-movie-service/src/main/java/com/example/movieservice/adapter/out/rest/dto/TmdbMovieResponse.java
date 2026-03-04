package com.example.movieservice.adapter.out.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO que mapeia a resposta de detalhes de filme do TheMovieDB.
 * Formato: GET /movie/{id}
 *
 * Já implementado — o aluno NÃO precisa alterar este arquivo.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbMovieResponse(

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

        Double popularity,

        Integer runtime,

        String tagline,

        String status,

        List<Genre> genres

) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Genre(
            Integer id,
            String name
    ) {
    }
}
