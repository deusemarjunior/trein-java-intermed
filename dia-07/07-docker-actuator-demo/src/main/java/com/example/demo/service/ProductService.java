package com.example.demo.service;

import com.example.demo.dto.ProductCreatedEvent;
import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.messaging.ProductEventPublisher;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;

    public ProductService(ProductRepository productRepository,
                          ProductEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'all'")
    public List<ProductResponse> findAll() {
        log.info("Listando todos os produtos (CACHE MISS — buscando no banco)");
        List<Product> products = productRepository.findAll();
        log.debug("Total de produtos encontrados: {}", products.size());
        return products.stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponse findById(Long id) {
        MDC.put("productId", String.valueOf(id));
        log.info("Buscando produto por ID (CACHE MISS — buscando no banco)");

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Produto não encontrado com ID: {}", id);
                    return new ResourceNotFoundException("Product", id);
                });

        MDC.remove("productId");
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'category:' + #category")
    public List<ProductResponse> findByCategory(String category) {
        log.info("Buscando produtos por categoria: {} (CACHE MISS)", category);
        return productRepository.findByCategory(category).stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse create(ProductRequest request) {
        log.info("Criando produto: {}", request.name());

        Product product = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.category()
        );

        Product saved = productRepository.save(product);
        MDC.put("productId", String.valueOf(saved.getId()));
        log.info("Produto criado com sucesso: {} (ID: {})", saved.getName(), saved.getId());

        // Publicar evento no RabbitMQ
        ProductCreatedEvent event = new ProductCreatedEvent(
                saved.getId(),
                saved.getName(),
                saved.getCategory(),
                saved.getPrice(),
                saved.getStock(),
                LocalDateTime.now()
        );
        eventPublisher.publishProductCreated(event);

        MDC.remove("productId");
        return ProductResponse.from(saved);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse update(Long id, ProductRequest request) {
        MDC.put("productId", String.valueOf(id));
        log.info("Atualizando produto: {}", request.name());

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Produto não encontrado para atualização: {}", id);
                    return new ResourceNotFoundException("Product", id);
                });

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(request.category());

        Product updated = productRepository.save(product);
        log.info("Produto atualizado com sucesso");

        MDC.remove("productId");
        return ProductResponse.from(updated);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void delete(Long id) {
        MDC.put("productId", String.valueOf(id));
        log.info("Deletando produto");

        if (!productRepository.existsById(id)) {
            log.error("Tentativa de deletar produto inexistente: {}", id);
            throw new ResourceNotFoundException("Product", id);
        }

        productRepository.deleteById(id);
        log.info("Produto deletado com sucesso");
        MDC.remove("productId");
    }
}
