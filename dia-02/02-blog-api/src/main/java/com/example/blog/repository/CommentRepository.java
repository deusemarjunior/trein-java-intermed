package com.example.blog.repository;

import com.example.blog.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByPostId(Long postId);
    
    List<Comment> findByAuthor(String author);
    
    long countByPostId(Long postId);
    
    /**
     * Buscar comentário com o Post (JOIN FETCH)
     * Útil quando você precisa acessar dados do post
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.post WHERE c.id = :id")
    Comment findByIdWithPost(Long id);
}
