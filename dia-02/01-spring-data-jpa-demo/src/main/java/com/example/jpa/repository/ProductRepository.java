package com.example.jpa.repository;

import com.example.jpa.dto.product.ProductSummary;
import com.example.jpa.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // ========== Query Methods (Spring gera automaticamente) ==========
    
    List<Product> findByName(String name);
    
    List<Product> findByNameContainingIgnoreCase(String keyword);
    
    List<Product> findByPriceGreaterThan(BigDecimal price);
    
    List<Product> findByPriceLessThanEqual(BigDecimal price);
    
    List<Product> findByActive(Boolean active);
    
    List<Product> findByStockGreaterThan(Integer stock);
    
    Optional<Product> findByNameAndCategoryId(String name, Long categoryId);
    
    boolean existsByName(String name);
    
    long countByCategoryId(Long categoryId);
    
    // Paginação
    Page<Product> findByActive(Boolean active, Pageable pageable);
    
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    
    // ========== @Query JPQL ==========
    
    @Query("SELECT p FROM Product p WHERE p.active = true")
    List<Product> findAllActive();
    
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
    List<Product> findByPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
    
    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :categoryName")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);
    
    // JOIN FETCH para evitar N+1 problem
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);
    
    // Agregação
    @Query("SELECT AVG(p.price) FROM Product p WHERE p.category.id = :categoryId")
    BigDecimal getAveragePriceByCategory(@Param("categoryId") Long categoryId);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock > 0")
    long countInStock();
    
    @Query("SELECT SUM(p.stock) FROM Product p WHERE p.category.id = :categoryId")
    Long getTotalStockByCategory(@Param("categoryId") Long categoryId);
    
    // DTO Projection
    @Query("SELECT new com.example.jpa.dto.product.ProductSummary(p.id, p.name, p.price) " +
           "FROM Product p WHERE p.active = true ORDER BY p.name")
    List<ProductSummary> findAllSummaries();
    
    // ========== @Query Native SQL ==========
    
    @Query(value = "SELECT * FROM products WHERE LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%'))", 
           nativeQuery = true)
    List<Product> searchByKeywordNative(@Param("keyword") String keyword);
    
    @Query(value = "SELECT * FROM products WHERE price > :price AND category_id IN " +
                   "(SELECT id FROM categories WHERE active = true) ORDER BY price DESC",
           nativeQuery = true)
    List<Product> findExpensiveInActiveCategories(@Param("price") BigDecimal price);
    
    // ========== @Modifying - UPDATE/DELETE ==========
    
    @Modifying
    @Query("UPDATE Product p SET p.active = false WHERE p.stock = 0")
    int deactivateOutOfStock();
    
    @Modifying
    @Query("UPDATE Product p SET p.stock = :stock WHERE p.id = :id")
    int updateStock(@Param("id") Long id, @Param("stock") Integer stock);
    
    @Modifying
    @Query("DELETE FROM Product p WHERE p.createdAt < :date AND p.active = false")
    int deleteOldInactive(@Param("date") LocalDateTime date);
    
    // ========== Custom Complex Query ==========
    
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:active IS NULL OR p.active = :active)")
    Page<Product> searchProducts(
        @Param("name") String name,
        @Param("categoryId") Long categoryId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("active") Boolean active,
        Pageable pageable
    );
}
