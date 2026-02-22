package com.example.movieservice.adapter.in.web.dto;

public record TokenResponse(
        String token,
        String type,
        long expiresIn
) {
}
