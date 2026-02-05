package com.example.products.controller;

import com.example.products.dto.request.CreateProductRequest;
import com.example.products.dto.request.UpdateProductRequest;
import com.example.products.dto.response.ProductResponse;
import com.example.products.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller para gerenciar produtos
 * 
 * Endpoints:
 * - GET    /api/products       - Listar todos
 * - GET    /api/products/{id}  - Buscar por ID
 * - POST   /api/products       - Criar produto
 * - PUT    /api/products/{id}  - Atualizar produto
 * - DELETE /api/products/{id}  - Deletar produto
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService service;
    
    public ProductController(ProductService service) {
        this.service = service;
    }
    
    /**
     * GET /api/products
     * Listar todos os produtos
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll(
        @RequestParam(required = false) String category
    ) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(service.findByCategory(category));
        }
        return ResponseEntity.ok(service.findAll());
    }
    
    /**
     * GET /api/products/{id}
     * Buscar produto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    /**
     * POST /api/products
     * Criar novo produto
     * 
     * @Valid valida o request usando Bean Validation
     */
    @PostMapping
    public ResponseEntity<ProductResponse> create(
        @Valid @RequestBody CreateProductRequest request
    ) {
        ProductResponse created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * PUT /api/products/{id}
     * Atualizar produto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateProductRequest request
    ) {
        ProductResponse updated = service.update(id, request);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * DELETE /api/products/{id}
     * Deletar produto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}
