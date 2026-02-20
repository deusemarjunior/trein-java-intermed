package com.example.cleanarchitecture.adapter.out.persistence;

import com.example.cleanarchitecture.domain.model.Product;
import com.example.cleanarchitecture.domain.port.out.ProductRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter OUT — implementa o Port OUT usando Spring Data JPA.
 * O domínio não sabe que está usando JPA — só conhece a interface ProductRepositoryPort.
 */
@Component
public class JpaProductRepository implements ProductRepositoryPort {

    private final SpringDataProductRepository springDataRepository;

    public JpaProductRepository(SpringDataProductRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = ProductPersistenceMapper.toJpaEntity(product);
        ProductJpaEntity saved = springDataRepository.save(entity);
        return ProductPersistenceMapper.toDomainModel(saved);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return springDataRepository.findById(id)
                .map(ProductPersistenceMapper::toDomainModel);
    }

    @Override
    public List<Product> findAll() {
        return springDataRepository.findAll().stream()
                .map(ProductPersistenceMapper::toDomainModel)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public boolean existsBySku(String sku) {
        return springDataRepository.existsBySku(sku);
    }

    @Override
    public boolean existsBySkuAndIdNot(String sku, Long id) {
        return springDataRepository.existsBySkuAndIdNot(sku, id);
    }
}
