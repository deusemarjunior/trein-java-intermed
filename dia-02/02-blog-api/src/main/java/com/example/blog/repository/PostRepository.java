package com.example.blog.repository;

import com.example.blog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    List<Post> findByAuthor(String author);
    
    List<Post> findByTitleContainingIgnoreCase(String keyword);
    
    // ========== DEMONSTRAÇÃO DE N+1 PROBLEM E SOLUÇÃO ==========
    
    /**
     * ❌ PROBLEMA N+1:
     * Busca normal sem JOIN FETCH causa múltiplas queries:
     * 1 query para buscar o Post
     * N queries adicionais para cada Comment (quando acessado)
     * M queries adicionais para cada Tag (quando acessado)
     * 
     * Use findById() padrão e depois acesse post.getComments() 
     * para ver o problema acontecer!
     */
    
    /**
     * ✅ SOLUÇÃO: JOIN FETCH
     * Uma única query que traz Post + Comments + Tags
     * DISTINCT evita duplicações quando há múltiplos relacionamentos
     */
    @Query("SELECT DISTINCT p FROM Post p " +
           "LEFT JOIN FETCH p.comments " +
           "LEFT JOIN FETCH p.tags " +
           "WHERE p.id = :id")
    Optional<Post> findByIdWithCommentsAndTags(@Param("id") Long id);
    
    /**
     * Buscar post com apenas comments (sem tags)
     */
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.id = :id")
    Optional<Post> findByIdWithComments(@Param("id") Long id);
    
    /**
     * Buscar post com apenas tags (sem comments)
     */
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.tags WHERE p.id = :id")
    Optional<Post> findByIdWithTags(@Param("id") Long id);
    
    /**
     * Buscar posts por nome de tag
     */
    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE t.name = :tagName")
    List<Post> findByTagName(@Param("tagName") String tagName);
    
    /**
     * Contar comentários de um post
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    long countCommentsByPostId(@Param("postId") Long postId);
    
    /**
     * Posts mais comentados (ordenados por quantidade de comentários)
     */
    @Query("SELECT p FROM Post p LEFT JOIN p.comments c " +
           "GROUP BY p.id ORDER BY COUNT(c) DESC")
    List<Post> findMostCommented();
}
