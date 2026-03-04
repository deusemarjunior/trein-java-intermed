package com.example.jpa.service;

import com.example.jpa.dto.comment.CommentResponse;
import com.example.jpa.dto.comment.CreateCommentRequest;
import com.example.jpa.exception.ResourceNotFoundException;
import com.example.jpa.model.Comment;
import com.example.jpa.model.Post;
import com.example.jpa.repository.CommentRepository;
import com.example.jpa.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    
    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }
    
    public List<CommentResponse> findByPostId(Long postId) {
        // Validar se o post existe
        postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        
        return commentRepository.findByPostId(postId).stream()
            .map(CommentResponse::from)
            .toList();
    }
    
    @Transactional
    public CommentResponse create(Long postId, CreateCommentRequest request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        
        Comment comment = new Comment(request.text(), request.author());
        post.addComment(comment); // Usa o helper method para manter sincronização
        
        Comment saved = commentRepository.save(comment);
        return CommentResponse.from(saved);
    }
    
    @Transactional
    public void delete(Long id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        
        // Remove do post usando helper method
        Post post = comment.getPost();
        if (post != null) {
            post.removeComment(comment);
        }
        
        commentRepository.delete(comment);
    }
}
