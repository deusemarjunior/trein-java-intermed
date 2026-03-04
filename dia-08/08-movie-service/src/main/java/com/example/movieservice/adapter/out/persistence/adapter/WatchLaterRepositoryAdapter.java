package com.example.movieservice.adapter.out.persistence.adapter;

import com.example.movieservice.adapter.out.persistence.entity.WatchLaterEntity;
import com.example.movieservice.adapter.out.persistence.repository.WatchLaterJpaRepository;
import com.example.movieservice.domain.port.out.WatchLaterRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter de persistência para "assistir depois".
 * Já implementado — o aluno NÃO precisa alterar este arquivo.
 */
@Component
public class WatchLaterRepositoryAdapter implements WatchLaterRepositoryPort {

    private final WatchLaterJpaRepository jpaRepository;

    public WatchLaterRepositoryAdapter(WatchLaterJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WatchLaterEntity save(WatchLaterEntity watchLater) {
        return jpaRepository.save(watchLater);
    }

    @Override
    public Optional<WatchLaterEntity> findByMovieIdAndUserId(Long movieId, String userId) {
        return jpaRepository.findByMovieIdAndUserId(movieId, userId);
    }

    @Override
    public void deleteByMovieIdAndUserId(Long movieId, String userId) {
        jpaRepository.deleteByMovieIdAndUserId(movieId, userId);
    }

    @Override
    public boolean existsByMovieIdAndUserId(Long movieId, String userId) {
        return jpaRepository.existsByMovieIdAndUserId(movieId, userId);
    }
}
