package com.example.movieservice.domain.exception;

public class MaxFavoritesException extends RuntimeException {

    public MaxFavoritesException(int max) {
        super("Limite máximo de " + max + " favoritos atingido");
    }
}
