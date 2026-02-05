package com.example.jpa.controller;

import com.example.jpa.dto.comment.CommentResponse;
import com.example.jpa.dto.comment.CreateCommentRequest;
import com.example.jpa.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
    
    private final CommentService commentService;
    
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    
    @GetMapping
    public ResponseEntity<List<CommentResponse>> findByPostId(@PathVariable Long postId) {
        List<CommentResponse> comments = commentService.findByPostId(postId);
        return ResponseEntity.ok(comments);
    }
    
    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request) {
        
        CommentResponse comment = commentService.create(postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
    
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long postId,
            @PathVariable Long commentId) {
        
        commentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}
