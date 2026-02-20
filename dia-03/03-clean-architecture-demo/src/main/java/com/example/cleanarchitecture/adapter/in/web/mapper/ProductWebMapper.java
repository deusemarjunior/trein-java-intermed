package com.example.cleanarchitecture.adapter.in.web.mapper;

import com.example.cleanarchitecture.adapter.in.web.dto.ProductRequest;
import com.example.cleanarchitecture.adapter.in.web.dto.ProductResponse;
import com.example.cleanarchitecture.domain.model.Product;

/**
 * Mapper Web — converte entre DTOs (web) e modelo de domínio.
 */
public class ProductWebMapper {

    private ProductWebMapper() {
        // Utility class
    }

    /**
     * Converte ProductRequest (DTO entrada) → Product (modelo de domínio).
     */
    public static Product toModel(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setDescription(request.description());
        return product;
    }

    /**
     * Converte Product (modelo de domínio) → ProductResponse (DTO saída).
     */
    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getPrice(),
                product.getDescription(),
                product.getCreatedAt()
        );
    }
}
