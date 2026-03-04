package com.example.jpa.service;

import com.example.jpa.dto.category.CategoryResponse;
import com.example.jpa.dto.category.CreateCategoryRequest;
import com.example.jpa.exception.DuplicateResourceException;
import com.example.jpa.exception.ResourceNotFoundException;
import com.example.jpa.model.Category;
import com.example.jpa.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return CategoryResponse.from(category);
    }
    
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
            .map(CategoryResponse::from)
            .toList();
    }
    
    public List<CategoryResponse> findAllActive() {
        return categoryRepository.findByActive(true).stream()
            .map(CategoryResponse::from)
            .toList();
    }
    
    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Category", "name", request.name());
        }
        
        Category category = new Category(request.name(), request.description());
        Category saved = categoryRepository.save(category);
        return CategoryResponse.from(saved);
    }
    
    @Transactional
    public CategoryResponse update(Long id, CreateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        // Verificar duplicidade
        categoryRepository.findByName(request.name())
            .ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new DuplicateResourceException("Category", "name", request.name());
                }
            });
        
        category.setName(request.name());
        category.setDescription(request.description());
        
        Category updated = categoryRepository.save(category);
        return CategoryResponse.from(updated);
    }
    
    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", "id", id);
        }
        categoryRepository.deleteById(id);
    }
}
