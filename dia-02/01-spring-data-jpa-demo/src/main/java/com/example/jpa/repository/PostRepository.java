package com.example.jpa.repository;

import com.example.jpa.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    List<Post> findByPublished(Boolean published);
    
    List<Post> findByAuthor(String author);
    
    Page<Post> findByPublished(Boolean published, Pageable pageable);
    
    Page<Post> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    
    // JOIN FETCH para evitar N+1 (carregar comments e tags junto)
    @Query("SELECT DISTINCT p FROM Post p " +
           "LEFT JOIN FETCH p.comments " +
           "LEFT JOIN FETCH p.tags " +
           "WHERE p.id = :id")
    Optional<Post> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :tagName")
    List<Post> findByTagName(@Param("tagName") String tagName);
    
    @Query("SELECT COUNT(c) FROM Post p JOIN p.comments c WHERE p.id = :postId")
    long countCommentsByPostId(@Param("postId") Long postId);
}
