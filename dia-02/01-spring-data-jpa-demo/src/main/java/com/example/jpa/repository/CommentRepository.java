package com.example.jpa.repository;

import com.example.jpa.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByPostId(Long postId);
    
    List<Comment> findByAuthor(String author);
    
    long countByPostId(Long postId);
    
    @Query("SELECT c FROM Comment c JOIN FETCH c.post WHERE c.id = :id")
    Comment findByIdWithPost(Long id);
}
