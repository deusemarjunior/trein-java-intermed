package com.example.jpa.controller;

import com.example.jpa.dto.product.CreateProductRequest;
import com.example.jpa.dto.product.ProductResponse;
import com.example.jpa.dto.product.ProductSummary;
import com.example.jpa.dto.product.UpdateProductRequest;
import com.example.jpa.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        ProductResponse product = productService.findById(id);
        return ResponseEntity.ok(product);
    }
    
    // GET /api/products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        List<ProductResponse> products = productService.findAll();
        return ResponseEntity.ok(products);
    }
    
    // GET /api/products/paged?page=0&size=10&sort=name,asc
    @GetMapping("/paged")
    public ResponseEntity<Page<ProductResponse>> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        
        // Construir ordenação
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(direction, sort[0]);
        
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<ProductResponse> products = productService.findAllPaged(pageable);
        
        return ResponseEntity.ok(products);
    }
    
    // GET /api/products/search?name=laptop&categoryId=1&minPrice=1000&maxPrice=5000&active=true&page=0&size=10
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        
        Page<ProductResponse> products = productService.searchProducts(
            name, categoryId, minPrice, maxPrice, active, pageable
        );
        
        return ResponseEntity.ok(products);
    }
    
    // GET /api/products/summaries
    @GetMapping("/summaries")
    public ResponseEntity<List<ProductSummary>> findAllSummaries() {
        List<ProductSummary> summaries = productService.findAllSummaries();
        return ResponseEntity.ok(summaries);
    }
    
    // GET /api/products/category/{categoryId}
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> findByCategory(@PathVariable Long categoryId) {
        List<ProductResponse> products = productService.findByCategory(categoryId);
        return ResponseEntity.ok(products);
    }
    
    // GET /api/products/category/{categoryId}/average-price
    @GetMapping("/category/{categoryId}/average-price")
    public ResponseEntity<BigDecimal> getAveragePriceByCategory(@PathVariable Long categoryId) {
        BigDecimal average = productService.getAveragePriceByCategory(categoryId);
        return ResponseEntity.ok(average);
    }
    
    // POST /api/products
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse product = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    // PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        
        ProductResponse product = productService.update(id, request);
        return ResponseEntity.ok(product);
    }
    
    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    // POST /api/products/deactivate-out-of-stock
    @PostMapping("/deactivate-out-of-stock")
    public ResponseEntity<Integer> deactivateOutOfStock() {
        int count = productService.deactivateOutOfStock();
        return ResponseEntity.ok(count);
    }
}
