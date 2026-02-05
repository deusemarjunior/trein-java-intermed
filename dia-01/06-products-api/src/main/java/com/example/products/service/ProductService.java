package com.example.products.service;

import com.example.products.dto.request.CreateProductRequest;
import com.example.products.dto.request.UpdateProductRequest;
import com.example.products.dto.response.ProductResponse;
import com.example.products.exception.ProductNotFoundException;
import com.example.products.model.Product;
import com.example.products.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service com lógica de negócio para produtos
 */
@Service
public class ProductService {
    
    private final ProductRepository repository;
    
    // Constructor injection (recomendado, não precisa @Autowired)
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Listar todos os produtos
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return repository.findAll()
            .stream()
            .map(ProductResponse::from)
            .toList();  // Java 16+ (antes: .collect(Collectors.toList()))
    }
    
    /**
     * Buscar produto por ID
     */
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = repository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return ProductResponse.from(product);
    }
    
    /**
     * Buscar produtos por categoria
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findByCategory(String category) {
        return repository.findByCategory(category)
            .stream()
            .map(ProductResponse::from)
            .toList();
    }
    
    /**
     * Criar novo produto
     */
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Product product = new Product(
            request.name(),
            request.description(),
            request.price(),
            request.category()
        );
        
        Product saved = repository.save(product);
        return ProductResponse.from(saved);
    }
    
    /**
     * Atualizar produto existente
     */
    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = repository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        
        // Atualizar campos
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());
        
        Product updated = repository.save(product);
        return ProductResponse.from(updated);
    }
    
    /**
     * Deletar produto
     */
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
