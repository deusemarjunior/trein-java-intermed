package com.example.movieservice.domain.exception;

public class MovieAlreadyFavoritedException extends RuntimeException {

    public MovieAlreadyFavoritedException(Long movieId) {
        super("Filme " + movieId + " já está nos favoritos");
    }
}
