package com.example.products.repository;

import com.example.products.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

/**
 * Repository para acesso a dados de produtos
 * Spring Data JPA cria a implementação automaticamente!
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Query methods - Spring cria as queries automaticamente pelo nome do método!
    
    /**
     * Buscar produtos por categoria
     */
    List<Product> findByCategory(String category);
    
    /**
     * Buscar produtos por nome (case-insensitive, contém)
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    
    /**
     * Verificar se existe produto com o nome
     */
    boolean existsByName(String name);
    
    /**
     * Buscar produtos com preço maior que o especificado
     */
    List<Product> findByPriceGreaterThan(BigDecimal price);
    
    /**
     * Buscar produtos por categoria e preço menor que
     */
    List<Product> findByCategoryAndPriceLessThan(String category, BigDecimal price);
}
