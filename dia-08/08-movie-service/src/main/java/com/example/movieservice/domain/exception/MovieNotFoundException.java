package com.example.movieservice.domain.exception;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(Long movieId) {
        super("Filme não encontrado com ID: " + movieId);
    }
}
