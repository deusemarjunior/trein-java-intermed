package com.example.jpa.controller;

import com.example.jpa.dto.post.CreatePostRequest;
import com.example.jpa.dto.post.PostResponse;
import com.example.jpa.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    private final PostService postService;
    
    public PostController(PostService postService) {
        this.postService = postService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> findById(@PathVariable Long id) {
        PostResponse post = postService.findById(id);
        return ResponseEntity.ok(post);
    }
    
    @GetMapping
    public ResponseEntity<List<PostResponse>> findAll() {
        List<PostResponse> posts = postService.findAll();
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/paged")
    public ResponseEntity<Page<PostResponse>> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostResponse> posts = postService.findAllPaged(pageable);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/published")
    public ResponseEntity<List<PostResponse>> findPublished() {
        List<PostResponse> posts = postService.findByPublished(true);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/drafts")
    public ResponseEntity<List<PostResponse>> findDrafts() {
        List<PostResponse> posts = postService.findByPublished(false);
        return ResponseEntity.ok(posts);
    }
    
    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody CreatePostRequest request) {
        PostResponse post = postService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreatePostRequest request) {
        
        PostResponse post = postService.update(id, request);
        return ResponseEntity.ok(post);
    }
    
    @PostMapping("/{id}/publish")
    public ResponseEntity<PostResponse> publish(@PathVariable Long id) {
        PostResponse post = postService.publish(id);
        return ResponseEntity.ok(post);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
