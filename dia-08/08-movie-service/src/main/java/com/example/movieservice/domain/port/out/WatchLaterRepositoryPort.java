package com.example.movieservice.domain.port.out;

import com.example.movieservice.adapter.out.persistence.entity.WatchLaterEntity;

import java.util.Optional;

/**
 * Port de saída para persistência de "assistir depois".
 * Já implementado — o aluno NÃO precisa alterar este arquivo.
 */
public interface WatchLaterRepositoryPort {

    WatchLaterEntity save(WatchLaterEntity watchLater);

    Optional<WatchLaterEntity> findByMovieIdAndUserId(Long movieId, String userId);

    void deleteByMovieIdAndUserId(Long movieId, String userId);

    boolean existsByMovieIdAndUserId(Long movieId, String userId);
}
