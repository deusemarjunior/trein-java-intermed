package com.example.jpa.service;

import com.example.jpa.dto.post.CreatePostRequest;
import com.example.jpa.dto.post.PostResponse;
import com.example.jpa.exception.ResourceNotFoundException;
import com.example.jpa.model.Post;
import com.example.jpa.model.Tag;
import com.example.jpa.repository.PostRepository;
import com.example.jpa.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class PostService {
    
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    
    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }
    
    public PostResponse findById(Long id) {
        Post post = postRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return PostResponse.from(post);
    }
    
    public List<PostResponse> findAll() {
        return postRepository.findAll().stream()
            .map(PostResponse::from)
            .toList();
    }
    
    public Page<PostResponse> findAllPaged(Pageable pageable) {
        return postRepository.findAll(pageable)
            .map(PostResponse::from);
    }
    
    public List<PostResponse> findByPublished(Boolean published) {
        return postRepository.findByPublished(published).stream()
            .map(PostResponse::from)
            .toList();
    }
    
    @Transactional
    public PostResponse create(CreatePostRequest request) {
        Post post = new Post(request.title(), request.content(), request.author());
        
        // Adicionar tags
        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (Long tagId : request.tagIds()) {
                Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));
                tags.add(tag);
            }
            post.setTags(tags);
        }
        
        Post saved = postRepository.save(post);
        return PostResponse.from(saved);
    }
    
    @Transactional
    public PostResponse update(Long id, CreatePostRequest request) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setAuthor(request.author());
        
        // Atualizar tags
        if (request.tagIds() != null) {
            post.getTags().clear();
            for (Long tagId : request.tagIds()) {
                Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));
                post.getTags().add(tag);
            }
        }
        
        Post updated = postRepository.save(post);
        return PostResponse.from(updated);
    }
    
    @Transactional
    public PostResponse publish(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        
        post.setPublished(true);
        Post updated = postRepository.save(post);
        return PostResponse.from(updated);
    }
    
    @Transactional
    public void delete(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post", "id", id);
        }
        postRepository.deleteById(id);
    }
}
