package com.example.movieservice.domain.model;

/**
 * Modelo de domínio representando um filme.
 * Este record é usado internamente no domínio — independente do TheMovieDB ou do banco.
 */
public record Movie(
        Long id,
        String title,
        String overview,
        String posterPath,
        String backdropPath,
        String releaseDate,
        Double voteAverage,
        Integer voteCount,
        Double popularity,
        boolean favorite,
        boolean watchLater
) {
    /**
     * Cria uma cópia do filme com status de favorito atualizado.
     */
    public Movie withFavorite(boolean favorite) {
        return new Movie(id, title, overview, posterPath, backdropPath,
                releaseDate, voteAverage, voteCount, popularity, favorite, watchLater);
    }

    /**
     * Cria uma cópia do filme com status de "assistir depois" atualizado.
     */
    public Movie withWatchLater(boolean watchLater) {
        return new Movie(id, title, overview, posterPath, backdropPath,
                releaseDate, voteAverage, voteCount, popularity, favorite, watchLater);
    }
}
