package com.example.testingdemo.mapper;

import com.example.testingdemo.dto.ProductRequest;
import com.example.testingdemo.dto.ProductResponse;
import com.example.testingdemo.model.Product;

public class ProductMapper {

    private ProductMapper() {
    }

    public static Product toEntity(ProductRequest request) {
        return new Product(
                request.name(),
                request.sku(),
                request.price(),
                request.quantity()
        );
    }

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getPrice(),
                product.getQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public static void updateEntity(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
    }
}
