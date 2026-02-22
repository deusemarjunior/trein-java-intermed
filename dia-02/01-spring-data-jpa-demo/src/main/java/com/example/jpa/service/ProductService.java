package com.example.jpa.service;

import com.example.jpa.dto.product.CreateProductRequest;
import com.example.jpa.dto.product.ProductResponse;
import com.example.jpa.dto.product.ProductSummary;
import com.example.jpa.dto.product.UpdateProductRequest;
import com.example.jpa.exception.BusinessException;
import com.example.jpa.exception.DuplicateResourceException;
import com.example.jpa.exception.ResourceNotFoundException;
import com.example.jpa.model.Category;
import com.example.jpa.model.Product;
import com.example.jpa.repository.CategoryRepository;
import com.example.jpa.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    
    public ProductResponse findById(Long id) {
        Product product = productRepository.findByIdWithCategory(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return ProductResponse.from(product);
    }
    
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
            .map(ProductResponse::from)
            .toList();
    }
    
    public Page<ProductResponse> findAllPaged(Pageable pageable) {
        return productRepository.findAll(pageable)
            .map(ProductResponse::from);
    }
    
    public Page<ProductResponse> searchProducts(
            String name, 
            Long categoryId, 
            BigDecimal minPrice, 
            BigDecimal maxPrice,
            Boolean active,
            Pageable pageable) {
        
        return productRepository.searchProducts(name, categoryId, minPrice, maxPrice, active, pageable)
            .map(ProductResponse::from);
    }
    
    public List<ProductSummary> findAllSummaries() {
        return productRepository.findAllSummaries();
    }
    
    public List<ProductResponse> findByCategory(Long categoryId) {
        // Validar se a categoria existe
        categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        return productRepository.findByCategoryId(categoryId, Pageable.unpaged())
            .map(ProductResponse::from)
            .toList();
    }
    
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        // Validar duplicidade
        if (productRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Product", "name", request.name());
        }
        
        // Buscar categoria
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.categoryId()));
        
        // Validar categoria ativa
        if (!category.getActive()) {
            throw new BusinessException("Não é possível criar produto em categoria inativa");
        }
        
        // Criar produto
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setImageUrl(request.imageUrl());
        product.setCategory(category);
        
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }
    
    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        // Atualizar apenas campos fornecidos
        if (request.name() != null) {
            // Verificar duplicidade (exceto o próprio produto)
            productRepository.findByName(request.name())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException("Product", "name", request.name());
                    }
                });
            product.setName(request.name());
        }
        
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        
        if (request.stock() != null) {
            product.setStock(request.stock());
        }
        
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.categoryId()));
            product.setCategory(category);
        }
        
        if (request.imageUrl() != null) {
            product.setImageUrl(request.imageUrl());
        }
        
        if (request.active() != null) {
            product.setActive(request.active());
        }
        
        Product updated = productRepository.save(product);
        return ProductResponse.from(updated);
    }
    
    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
    }
    
    @Transactional
    public int deactivateOutOfStock() {
        return productRepository.deactivateOutOfStock();
    }
    
    public BigDecimal getAveragePriceByCategory(Long categoryId) {
        categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        return productRepository.getAveragePriceByCategory(categoryId);
    }
}
