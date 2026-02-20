package com.example.cleanarchitecture.adapter.out.persistence;

import com.example.cleanarchitecture.domain.model.Product;

/**
 * Mapper de Persistência — converte entre modelo de domínio e entidade JPA.
 */
public class ProductPersistenceMapper {

    private ProductPersistenceMapper() {
        // Utility class
    }

    /**
     * Converte Product (domínio) → ProductJpaEntity (JPA).
     */
    public static ProductJpaEntity toJpaEntity(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setSku(product.getSku());
        entity.setPrice(product.getPrice());
        entity.setDescription(product.getDescription());
        entity.setCreatedAt(product.getCreatedAt());
        entity.setUpdatedAt(product.getUpdatedAt());
        return entity;
    }

    /**
     * Converte ProductJpaEntity (JPA) → Product (domínio).
     */
    public static Product toDomainModel(ProductJpaEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getSku(),
                entity.getPrice(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
