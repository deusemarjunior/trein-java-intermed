package com.example.movieservice.adapter.out.persistence.repository;

import com.example.movieservice.adapter.out.persistence.entity.WatchLaterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WatchLaterJpaRepository extends JpaRepository<WatchLaterEntity, Long> {

    Optional<WatchLaterEntity> findByMovieIdAndUserId(Long movieId, String userId);

    void deleteByMovieIdAndUserId(Long movieId, String userId);

    boolean existsByMovieIdAndUserId(Long movieId, String userId);
}
