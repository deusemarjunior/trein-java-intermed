package com.example.demo.mapper;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.model.Product;

public class ProductMapper {

    private ProductMapper() {}

    public static Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setDescription(request.description());
        return product;
    }

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

    public static void updateEntity(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setDescription(request.description());
    }
}
