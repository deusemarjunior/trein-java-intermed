package com.example.testingdemo.service;

import com.example.testingdemo.dto.ProductRequest;
import com.example.testingdemo.dto.ProductResponse;
import com.example.testingdemo.exception.DuplicateSkuException;
import com.example.testingdemo.exception.ProductNotFoundException;
import com.example.testingdemo.mapper.ProductMapper;
import com.example.testingdemo.model.Product;
import com.example.testingdemo.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<ProductResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    public ProductResponse findById(Long id) {
        return repository.findById(id)
                .map(ProductMapper::toResponse)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public ProductResponse findBySku(String sku) {
        return repository.findBySku(sku)
                .map(ProductMapper::toResponse)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado com SKU: " + sku));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (repository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }

        Product product = ProductMapper.toEntity(request);
        Product saved = repository.save(product);
        return ProductMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Verifica se o novo SKU já existe para outro produto
        repository.findBySku(request.sku())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateSkuException(request.sku());
                });

        ProductMapper.updateEntity(product, request);
        Product saved = repository.save(product);
        return ProductMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        repository.deleteById(id);
    }

    public List<ProductResponse> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return repository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    public List<ProductResponse> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    public Page<ProductResponse> findInStock(Pageable pageable) {
        return repository.findByQuantityGreaterThan(0, pageable)
                .map(ProductMapper::toResponse);
    }
}
