package com.example.movieservice.adapter.out.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO que mapeia a resposta de créditos do TheMovieDB.
 * Formato: GET /movie/{id}/credits
 *
 * Já implementado — o aluno NÃO precisa alterar este arquivo.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbCreditsResponse(

        Long id,

        List<TmdbCast> cast,

        List<TmdbCrew> crew

) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmdbCast(

            Long id,

            String name,

            String character,

            @JsonProperty("profile_path")
            String profilePath
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmdbCrew(

            Long id,

            String name,

            String job,

            @JsonProperty("known_for_department")
            String department,

            @JsonProperty("profile_path")
            String profilePath
    ) {
    }
}
