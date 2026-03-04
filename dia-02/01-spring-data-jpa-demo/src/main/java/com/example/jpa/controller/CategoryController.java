package com.example.jpa.controller;

import com.example.jpa.dto.category.CategoryResponse;
import com.example.jpa.dto.category.CreateCategoryRequest;
import com.example.jpa.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Gerenciamento de categorias de produtos")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @Operation(summary = "Buscar categoria por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        CategoryResponse category = categoryService.findById(id);
        return ResponseEntity.ok(category);
    }
    
    @Operation(summary = "Listar todas as categorias")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll() {
        List<CategoryResponse> categories = categoryService.findAll();
        return ResponseEntity.ok(categories);
    }
    
    @Operation(summary = "Listar categorias ativas")
    @GetMapping("/active")
    public ResponseEntity<List<CategoryResponse>> findAllActive() {
        List<CategoryResponse> categories = categoryService.findAllActive();
        return ResponseEntity.ok(categories);
    }
    
    @Operation(summary = "Criar categoria")
    @ApiResponse(responseCode = "201", description = "Categoria criada")
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse category = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }
    
    @Operation(summary = "Atualizar categoria")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateCategoryRequest request) {
        
        CategoryResponse category = categoryService.update(id, request);
        return ResponseEntity.ok(category);
    }
    
    @Operation(summary = "Deletar categoria")
    @ApiResponse(responseCode = "204", description = "Categoria deletada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
