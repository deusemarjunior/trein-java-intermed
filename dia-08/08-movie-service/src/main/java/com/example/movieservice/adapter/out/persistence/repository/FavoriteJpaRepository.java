package com.example.movieservice.adapter.out.persistence.repository;

import com.example.movieservice.adapter.out.persistence.entity.FavoriteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteJpaRepository extends JpaRepository<FavoriteEntity, Long> {

    Optional<FavoriteEntity> findByMovieIdAndUserId(Long movieId, String userId);

    Page<FavoriteEntity> findByUserId(String userId, Pageable pageable);

    void deleteByMovieIdAndUserId(Long movieId, String userId);

    long countByUserId(String userId);

    boolean existsByMovieIdAndUserId(Long movieId, String userId);
}
