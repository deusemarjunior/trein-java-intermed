package com.example.blog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade Comment - Lado proprietário do relacionamento ManyToOne com Post
 * 
 * IMPORTANTE: 
 * - Esta classe TEM a Foreign Key (post_id)
 * - Usa FetchType.LAZY para não carregar o Post desnecessariamente
 */
@Entity
@Table(name = "comments")
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    
    @Column(nullable = false, length = 100)
    private String author;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // ========== RELACIONAMENTO ManyToOne ==========
    // Muitos Comments pertencem a um Post
    // @JoinColumn define a coluna FK na tabela comments
    // fetch = FetchType.LAZY - NÃO carrega o Post automaticamente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public Comment() {}
    
    public Comment(String text, String author) {
        this.text = text;
        this.author = author;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Post getPost() {
        return post;
    }
    
    public void setPost(Post post) {
        this.post = post;
    }
}
