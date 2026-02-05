package com.example.blog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entidade Post - Demonstra relacionamentos OneToMany e ManyToMany
 * 
 * Relacionamentos:
 * - OneToMany com Comment (um post tem muitos comentários)
 * - ManyToMany com Tag (um post tem muitas tags, uma tag está em muitos posts)
 */
@Entity
@Table(name = "posts")
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false, length = 100)
    private String author;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ========== RELACIONAMENTO OneToMany ==========
    // Um Post tem muitos Comments
    // mappedBy = "post" indica que Comment é o lado proprietário (tem a FK)
    // cascade = CascadeType.ALL - operações em Post afetam Comments
    // orphanRemoval = true - se remover comment da lista, ele é deletado do BD
    @OneToMany(
        mappedBy = "post",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY  // LAZY é o padrão, busca comments apenas quando acessado
    )
    private List<Comment> comments = new ArrayList<>();
    
    // ========== RELACIONAMENTO ManyToMany ==========
    // Muitos Posts podem ter muitas Tags
    // @JoinTable cria a tabela intermediária post_tags
    @ManyToMany(
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        fetch = FetchType.LAZY  // LAZY é o padrão
    )
    @JoinTable(
        name = "post_tags",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // ========== HELPER METHODS ==========
    // Métodos para manter sincronização em relacionamentos bidirecionais
    
    /**
     * Adiciona um comentário ao post
     * IMPORTANTE: Mantém sincronização bidirecional
     */
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }
    
    /**
     * Remove um comentário do post
     * IMPORTANTE: Quebra a relação bidirecional antes de remover
     */
    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }
    
    /**
     * Adiciona uma tag ao post
     * IMPORTANTE: Atualiza ambos os lados do relacionamento
     */
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getPosts().add(this);
    }
    
    /**
     * Remove uma tag do post
     * IMPORTANTE: Remove de ambos os lados
     */
    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getPosts().remove(this);
    }
    
    // Constructors
    public Post() {}
    
    public Post(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
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
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<Comment> getComments() {
        return comments;
    }
    
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    
    public Set<Tag> getTags() {
        return tags;
    }
    
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
