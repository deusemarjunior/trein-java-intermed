package com.example.movieservice.adapter.out.rest.dto;

import com.example.movieservice.domain.model.Movie;
import com.example.movieservice.domain.model.MovieCredits;
import com.example.movieservice.domain.model.MoviePage;

import java.util.List;

/**
 * Mapper que converte DTOs do TheMovieDB para modelos do domínio.
 * Já implementado — o aluno NÃO precisa alterar este arquivo.
 */
public final class TmdbMovieMapper {

    private TmdbMovieMapper() {
        // utility class
    }

    /**
     * Converte TmdbSearchResponse → MoviePage (domínio).
     */
    public static MoviePage toMoviePage(TmdbSearchResponse response) {
        List<Movie> movies = response.results().stream()
                .map(TmdbMovieMapper::toMovie)
                .toList();

        return new MoviePage(movies, response.page(), response.totalPages(), response.totalResults());
    }

    /**
     * Converte TmdbMovieResult (da busca) → Movie (domínio).
     */
    public static Movie toMovie(TmdbSearchResponse.TmdbMovieResult result) {
        return new Movie(
                result.id(),
                result.title(),
                result.overview(),
                result.posterPath(),
                result.backdropPath(),
                result.releaseDate(),
                result.voteAverage(),
                result.voteCount(),
                result.popularity(),
                false,
                false
        );
    }

    /**
     * Converte TmdbMovieResponse (detalhes) → Movie (domínio).
     */
    public static Movie toMovie(TmdbMovieResponse response) {
        return new Movie(
                response.id(),
                response.title(),
                response.overview(),
                response.posterPath(),
                response.backdropPath(),
                response.releaseDate(),
                response.voteAverage(),
                response.voteCount(),
                response.popularity(),
                false,
                false
        );
    }

    /**
     * Converte TmdbCreditsResponse → MovieCredits (domínio).
     */
    public static MovieCredits toMovieCredits(TmdbCreditsResponse response) {
        List<MovieCredits.CastMember> cast = response.cast().stream()
                .map(c -> new MovieCredits.CastMember(c.id(), c.name(), c.character(), c.profilePath()))
                .toList();

        List<MovieCredits.CrewMember> crew = response.crew().stream()
                .map(c -> new MovieCredits.CrewMember(c.id(), c.name(), c.job(), c.department(), c.profilePath()))
                .toList();

        return new MovieCredits(response.id(), cast, crew);
    }
}
