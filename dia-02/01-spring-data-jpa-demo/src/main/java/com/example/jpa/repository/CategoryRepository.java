package com.example.jpa.repository;

import com.example.jpa.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByName(String name);
    
    List<Category> findByActive(Boolean active);
    
    boolean existsByName(String name);
    
    @Query("SELECT c FROM Category c WHERE c.active = true ORDER BY c.name")
    List<Category> findAllActiveOrdered();
    
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Optional<Category> findByIdWithProducts(Long id);
}
