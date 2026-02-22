package com.example.movieservice.domain.port.out;

import com.example.movieservice.adapter.out.persistence.entity.FavoriteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Port de saída para persistência de favoritos.
 * Já implementado — o aluno NÃO precisa alterar este arquivo.
 */
public interface FavoriteRepositoryPort {

    FavoriteEntity save(FavoriteEntity favorite);

    Optional<FavoriteEntity> findByMovieIdAndUserId(Long movieId, String userId);

    Page<FavoriteEntity> findByUserId(String userId, Pageable pageable);

    void deleteByMovieIdAndUserId(Long movieId, String userId);

    long countByUserId(String userId);

    boolean existsByMovieIdAndUserId(Long movieId, String userId);
}
