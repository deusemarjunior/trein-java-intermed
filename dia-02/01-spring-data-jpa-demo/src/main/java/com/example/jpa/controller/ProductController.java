package com.example.jpa.controller;

import com.example.jpa.dto.product.CreateProductRequest;
import com.example.jpa.dto.product.ProductResponse;
import com.example.jpa.dto.product.ProductSummary;
import com.example.jpa.dto.product.UpdateProductRequest;
import com.example.jpa.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Products", description = "Gerenciamento de produtos com paginação, busca e projeções")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto pelo seu identificador")
    @ApiResponse(responseCode = "200", description = "Produto encontrado")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        ProductResponse product = productService.findById(id);
        return ResponseEntity.ok(product);
    }
    
    @Operation(summary = "Listar todos os produtos")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        List<ProductResponse> products = productService.findAll();
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Listar produtos com paginação", description = "Retorna produtos paginados com ordenação configurável")
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
    
    @Operation(summary = "Busca avançada de produtos", description = "Busca com filtros dinâmicos: nome, categoria, faixa de preço e status")
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
    
    @Operation(summary = "Listar resumos de produtos", description = "Retorna projeções resumidas (Projection) dos produtos")
    @GetMapping("/summaries")
    public ResponseEntity<List<ProductSummary>> findAllSummaries() {
        List<ProductSummary> summaries = productService.findAllSummaries();
        return ResponseEntity.ok(summaries);
    }
    
    @Operation(summary = "Listar produtos por categoria")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> findByCategory(@PathVariable Long categoryId) {
        List<ProductResponse> products = productService.findByCategory(categoryId);
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Preço médio por categoria", description = "Calcula o preço médio dos produtos de uma categoria")
    @GetMapping("/category/{categoryId}/average-price")
    public ResponseEntity<BigDecimal> getAveragePriceByCategory(@PathVariable Long categoryId) {
        BigDecimal average = productService.getAveragePriceByCategory(categoryId);
        return ResponseEntity.ok(average);
    }
    
    @Operation(summary = "Criar produto", description = "Cria um novo produto validando os campos obrigatórios")
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse product = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    @Operation(summary = "Atualizar produto")
    @ApiResponse(responseCode = "200", description = "Produto atualizado")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        
        ProductResponse product = productService.update(id, request);
        return ResponseEntity.ok(product);
    }
    
    @Operation(summary = "Deletar produto")
    @ApiResponse(responseCode = "204", description = "Produto deletado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Desativar sem estoque", description = "Desativa todos os produtos sem estoque")
    @PostMapping("/deactivate-out-of-stock")
    public ResponseEntity<Integer> deactivateOutOfStock() {
        int count = productService.deactivateOutOfStock();
        return ResponseEntity.ok(count);
    }
}
