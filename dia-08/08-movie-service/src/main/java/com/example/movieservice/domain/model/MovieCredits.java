package com.example.movieservice.domain.model;

import java.util.List;

/**
 * Modelo de domínio representando os créditos de um filme (elenco e equipe).
 */
public record MovieCredits(
        Long movieId,
        List<CastMember> cast,
        List<CrewMember> crew
) {
    public record CastMember(
            Long id,
            String name,
            String character,
            String profilePath
    ) {
    }

    public record CrewMember(
            Long id,
            String name,
            String job,
            String department,
            String profilePath
    ) {
    }
}
