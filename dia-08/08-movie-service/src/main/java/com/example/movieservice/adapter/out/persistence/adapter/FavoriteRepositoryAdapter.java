package com.example.movieservice.adapter.out.persistence.adapter;

import com.example.movieservice.adapter.out.persistence.entity.FavoriteEntity;
import com.example.movieservice.adapter.out.persistence.repository.FavoriteJpaRepository;
import com.example.movieservice.domain.port.out.FavoriteRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter de persistência para favoritos.
 * Implementa o Port de saída delegando ao JPA Repository.
 * Já implementado — o aluno NÃO precisa alterar este arquivo.
 */
@Component
public class FavoriteRepositoryAdapter implements FavoriteRepositoryPort {

    private final FavoriteJpaRepository jpaRepository;

    public FavoriteRepositoryAdapter(FavoriteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public FavoriteEntity save(FavoriteEntity favorite) {
        return jpaRepository.save(favorite);
    }

    @Override
    public Optional<FavoriteEntity> findByMovieIdAndUserId(Long movieId, String userId) {
        return jpaRepository.findByMovieIdAndUserId(movieId, userId);
    }

    @Override
    public Page<FavoriteEntity> findByUserId(String userId, Pageable pageable) {
        return jpaRepository.findByUserId(userId, pageable);
    }

    @Override
    public void deleteByMovieIdAndUserId(Long movieId, String userId) {
        jpaRepository.deleteByMovieIdAndUserId(movieId, userId);
    }

    @Override
    public long countByUserId(String userId) {
        return jpaRepository.countByUserId(userId);
    }

    @Override
    public boolean existsByMovieIdAndUserId(Long movieId, String userId) {
        return jpaRepository.existsByMovieIdAndUserId(movieId, userId);
    }
}
