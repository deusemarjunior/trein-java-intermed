package com.example.cleanarchitecture.domain.port.out;

import com.example.cleanarchitecture.domain.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Port OUT — interface que o domínio PRECISA da infraestrutura.
 * O adapter out (JPA) implementa esta interface.
 */
public interface ProductRepositoryPort {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findAll();

    void deleteById(Long id);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);
}
