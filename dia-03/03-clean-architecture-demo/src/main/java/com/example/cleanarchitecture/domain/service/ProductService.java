package com.example.cleanarchitecture.domain.service;

import com.example.cleanarchitecture.adapter.in.web.dto.ProductRequest;
import com.example.cleanarchitecture.adapter.in.web.dto.ProductResponse;
import com.example.cleanarchitecture.adapter.in.web.mapper.ProductWebMapper;
import com.example.cleanarchitecture.domain.exception.DuplicateSkuException;
import com.example.cleanarchitecture.domain.exception.ProductNotFoundException;
import com.example.cleanarchitecture.domain.model.Product;
import com.example.cleanarchitecture.domain.port.in.ProductUseCase;
import com.example.cleanarchitecture.domain.port.out.ProductRepositoryPort;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço de domínio — contém as regras de negócio.
 * Implementa o Port IN (ProductUseCase) e usa o Port OUT (ProductRepositoryPort).
 */
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort repositoryPort;

    public ProductService(ProductRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        validateUniqueSku(request.sku());

        Product product = ProductWebMapper.toModel(request);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        Product saved = repositoryPort.save(product);
        return ProductWebMapper.toResponse(saved);
    }

    @Override
    public ProductResponse findById(Long id) {
        Product product = repositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductWebMapper.toResponse(product);
    }

    @Override
    public List<ProductResponse> findAll() {
        return repositoryPort.findAll().stream()
                .map(ProductWebMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product existing = repositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (repositoryPort.existsBySkuAndIdNot(request.sku(), id)) {
            throw new DuplicateSkuException(request.sku());
        }

        existing.setName(request.name());
        existing.setSku(request.sku());
        existing.setPrice(request.price());
        existing.setDescription(request.description());
        existing.setUpdatedAt(LocalDateTime.now());

        Product updated = repositoryPort.save(existing);
        return ProductWebMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (repositoryPort.findById(id).isEmpty()) {
            throw new ProductNotFoundException(id);
        }
        repositoryPort.deleteById(id);
    }

    private void validateUniqueSku(String sku) {
        if (repositoryPort.existsBySku(sku)) {
            throw new DuplicateSkuException(sku);
        }
    }
}
