package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de categorias com Redis Cache.
 *
 * Demonstra as 3 annotations de cache:
 * - @Cacheable: retorna do cache se existir
 * - @CacheEvict: limpa o cache
 * - @CachePut: sempre executa e atualiza o cache
 */
@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * @Cacheable: Se o resultado já estiver no cache, retorna direto.
     * Caso contrário, executa o método e armazena no cache.
     *
     * Cache name: "categories"
     * Cache key: id do argumento
     */
    @Cacheable(value = "categories", key = "#id")
    @Transactional(readOnly = true)
    public Category findById(Long id) {
        log.info("🔍 [CACHE MISS] Buscando categoria #{} no banco de dados...", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    /**
     * Listagem de categorias — também cacheada.
     * key = "'all'" (string fixa) porque não tem argumento.
     */
    @Cacheable(value = "categories", key = "'all'")
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        log.info("🔍 [CACHE MISS] Buscando todas as categorias no banco de dados...");
        return categoryRepository.findAll();
    }

    /**
     * @CachePut: Sempre executa o método e atualiza o cache.
     * Útil após updates para manter o cache fresco.
     *
     * Também faz @CacheEvict da listagem completa.
     */
    @CachePut(value = "categories", key = "#id")
    @CacheEvict(value = "categories", key = "'all'")
    @Transactional
    public Category update(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        category.setName(name);
        category.setDescription(description);

        Category updated = categoryRepository.save(category);
        log.info("✏️ Categoria #{} atualizada e cache atualizado", id);
        return updated;
    }

    /**
     * @CacheEvict: Remove a entrada do cache.
     * allEntries = true remove TODAS as entradas do cache "categories".
     */
    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", id);
        }
        categoryRepository.deleteById(id);
        log.info("🗑️ Categoria #{} deletada e cache limpo", id);
    }
}
