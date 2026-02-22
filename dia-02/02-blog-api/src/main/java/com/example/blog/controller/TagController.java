package com.example.blog.controller;

import com.example.blog.model.Tag;
import com.example.blog.repository.TagRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "Gerenciamento de tags para categorização de posts")
public class TagController {
    
    private final TagRepository tagRepository;
    
    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    
    @Operation(summary = "Listar todas as tags")
    @GetMapping
    public ResponseEntity<List<Tag>> findAll() {
        return ResponseEntity.ok(tagRepository.findAll());
    }
    
    @Operation(summary = "Buscar tag por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Tag> findById(@PathVariable Long id) {
        return tagRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Criar nova tag")
    @ApiResponse(responseCode = "201", description = "Tag criada")
    @PostMapping
    public ResponseEntity<Tag> create(@RequestBody CreateTagRequest request) {
        Tag tag = new Tag(request.name(), request.color());
        Tag saved = tagRepository.save(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    public record CreateTagRequest(String name, String color) {}
}
