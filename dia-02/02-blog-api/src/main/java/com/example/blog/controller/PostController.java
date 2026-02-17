package com.example.blog.controller;

import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.Tag;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.TagRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller demonstrativo para o exercício de relacionamentos JPA
 * 
 * IMPORTANTE: Este controller é simplificado para focar nos conceitos de JPA.
 * Em produção, use DTOs, Services e Exception Handling adequados.
 */
@RestController
@RequestMapping("/api/posts")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Posts", description = "Gerenciamento de posts do blog com relacionamentos JPA")
public class PostController {
    
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    
    public PostController(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }
    
    /**
     * ❌ DEMONSTRAÇÃO: Problema N+1
     * Use este endpoint e veja nos logs múltiplas queries!
     */
    @Operation(summary = "Buscar post por ID", description = "❌ Demonstra problema N+1 - veja múltiplas queries nos logs")
    @GetMapping("/{id}")
    public ResponseEntity<Post> findById(@PathVariable Long id) {
        return postRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * ✅ SOLUÇÃO: JOIN FETCH
     * Use este endpoint e veja nos logs apenas 1 query!
     */
    @Operation(summary = "Buscar post com detalhes", description = "✅ Usa JOIN FETCH - apenas 1 query nos logs")
    @GetMapping("/{id}/with-details")
    public ResponseEntity<Post> findByIdWithDetails(@PathVariable Long id) {
        return postRepository.findByIdWithCommentsAndTags(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Listar todos os posts")
    @GetMapping
    public ResponseEntity<List<Post>> findAll() {
        return ResponseEntity.ok(postRepository.findAll());
    }
    
    @Operation(summary = "Buscar posts por autor")
    @GetMapping("/by-author/{author}")
    public ResponseEntity<List<Post>> findByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(postRepository.findByAuthor(author));
    }
    
    @Operation(summary = "Buscar posts por tag")
    @GetMapping("/by-tag/{tagName}")
    public ResponseEntity<List<Post>> findByTag(@PathVariable String tagName) {
        return ResponseEntity.ok(postRepository.findByTagName(tagName));
    }
    
    @Operation(summary = "Posts mais comentados")
    @GetMapping("/most-commented")
    public ResponseEntity<List<Post>> findMostCommented() {
        return ResponseEntity.ok(postRepository.findMostCommented());
    }
    
    @Operation(summary = "Criar novo post")
    @ApiResponse(responseCode = "201", description = "Post criado")
    @PostMapping
    public ResponseEntity<Post> create(@RequestBody CreatePostRequest request) {
        Post post = new Post(request.title(), request.content(), request.author());
        
        // Adicionar tags se fornecidas
        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (Long tagId : request.tagIds()) {
                tagRepository.findById(tagId).ifPresent(tags::add);
            }
            post.setTags(tags);
        }
        
        Post saved = postRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @Operation(summary = "Adicionar comentário ao post")
    @ApiResponse(responseCode = "201", description = "Comentário adicionado")
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long id,
            @RequestBody AddCommentRequest request) {
        
        return postRepository.findById(id)
            .map(post -> {
                Comment comment = new Comment(request.text(), request.author());
                post.addComment(comment);  // Usa helper method!
                postRepository.save(post);
                return ResponseEntity.status(HttpStatus.CREATED).body(comment);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Deletar post")
    @ApiResponse(responseCode = "204", description = "Post deletado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!postRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // Request records
    public record CreatePostRequest(String title, String content, String author, Set<Long> tagIds) {}
    public record AddCommentRequest(String text, String author) {}
}
