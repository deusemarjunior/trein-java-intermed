package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * GET /api/categories
     * Primeira chamada: CACHE MISS (log aparece)
     * Segunda chamada: CACHE HIT (log NÃO aparece)
     */
    @GetMapping
    public ResponseEntity<List<Category>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    /**
     * GET /api/categories/{id}
     * Demonstra @Cacheable individual.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    /**
     * PUT /api/categories/{id}
     * Demonstra @CachePut — atualiza o cache após update.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Category> update(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Category updated = categoryService.update(
                id,
                body.get("name"),
                body.get("description")
        );
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/categories/{id}
     * Demonstra @CacheEvict — limpa o cache.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
