package com.example.products.controller;

import com.example.products.dto.request.CreateProductRequest;
import com.example.products.dto.request.UpdateProductRequest;
import com.example.products.dto.response.ProductResponse;
import com.example.products.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller para gerenciar produtos
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "CRUD completo de produtos")
public class ProductController {
    
    private final ProductService service;
    
    public ProductController(ProductService service) {
        this.service = service;
    }
    
    /**
     * GET /api/products
     * Listar todos os produtos
     */
    @Operation(summary = "Listar produtos", description = "Lista todos os produtos, com filtro opcional por categoria")
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
    @Operation(summary = "Buscar produto por ID")
    @ApiResponse(responseCode = "200", description = "Produto encontrado")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
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
    @Operation(summary = "Criar produto")
    @ApiResponse(responseCode = "201", description = "Produto criado")
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
    @Operation(summary = "Atualizar produto")
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
    @Operation(summary = "Deletar produto")
    @ApiResponse(responseCode = "204", description = "Produto deletado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}
