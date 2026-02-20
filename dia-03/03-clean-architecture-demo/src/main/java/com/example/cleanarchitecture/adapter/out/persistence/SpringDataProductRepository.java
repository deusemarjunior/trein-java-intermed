package com.example.cleanarchitecture.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository — acesso ao banco via interface.
 */
@Repository
public interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, Long> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);
}
