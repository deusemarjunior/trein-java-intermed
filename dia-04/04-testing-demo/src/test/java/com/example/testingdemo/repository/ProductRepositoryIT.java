package com.example.testingdemo.repository;

import com.example.testingdemo.AbstractIntegrationTest;
import com.example.testingdemo.model.Product;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de integração do {@link ProductRepository} com Testcontainers.
 *
 * <p>Usa PostgreSQL real via container Docker para garantir que
 * as queries JPA funcionam corretamente em um banco de produção.</p>
 *
 * <p>Estende {@link AbstractIntegrationTest} que configura
 * o container PostgreSQL 16-alpine.</p>
 */
@DisplayName("ProductRepository — Testes de Integração")
class ProductRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    // ====================================================================
    // save & findById
    // ====================================================================
    @Nested
    @DisplayName("save() e findById()")
    class SaveAndFind {

        @Test
        @DisplayName("deve salvar e recuperar produto por id")
        void shouldSaveAndFindById() {
            // Arrange
            Product product = new Product("Notebook Dell", "NTB-001",
                    new BigDecimal("4599.90"), 10);

            // Act
            Product saved = repository.save(product);
            Optional<Product> found = repository.findById(saved.getId());

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Notebook Dell");
            assertThat(found.get().getSku()).isEqualTo("NTB-001");
            assertThat(found.get().getPrice()).isEqualByComparingTo(new BigDecimal("4599.90"));
            assertThat(found.get().getQuantity()).isEqualTo(10);
            assertThat(found.get().getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("deve gerar id automaticamente")
        void shouldGenerateId() {
            Product product = new Product("Mouse", "MSE-001",
                    new BigDecimal("99.90"), 50);

            Product saved = repository.save(product);

            assertThat(saved.getId()).isNotNull().isPositive();
        }
    }

    // ====================================================================
    // findBySku
    // ====================================================================
    @Nested
    @DisplayName("findBySku()")
    class FindBySku {

        @Test
        @DisplayName("deve encontrar produto por SKU")
        void shouldFindBySku() {
            Product product = new Product("Teclado", "KBD-001",
                    new BigDecimal("299.90"), 30);
            repository.save(product);

            Optional<Product> found = repository.findBySku("KBD-001");

            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Teclado");
        }

        @Test
        @DisplayName("deve retornar vazio para SKU inexistente")
        void shouldReturnEmptyForInvalidSku() {
            Optional<Product> found = repository.findBySku("INVALID");

            assertThat(found).isEmpty();
        }
    }

    // ====================================================================
    // UNIQUE constraint
    // ====================================================================
    @Nested
    @DisplayName("Restrição UNIQUE no SKU")
    class UniqueConstraint {

        @Test
        @DisplayName("deve rejeitar SKU duplicado")
        void shouldRejectDuplicateSku() {
            Product p1 = new Product("Produto A", "DUP-001",
                    new BigDecimal("100.00"), 5);
            repository.saveAndFlush(p1);

            Product p2 = new Product("Produto B", "DUP-001",
                    new BigDecimal("200.00"), 10);

            assertThatThrownBy(() -> repository.saveAndFlush(p2))
                    .isInstanceOf(Exception.class);
        }
    }

    // ====================================================================
    // findByPriceBetween
    // ====================================================================
    @Nested
    @DisplayName("findByPriceBetween()")
    class FindByPriceBetween {

        @Test
        @DisplayName("deve encontrar produtos na faixa de preço")
        void shouldFindByPriceRange() {
            repository.save(new Product("Barato", "B-001",
                    new BigDecimal("50.00"), 10));
            repository.save(new Product("Médio", "M-001",
                    new BigDecimal("500.00"), 10));
            repository.save(new Product("Caro", "C-001",
                    new BigDecimal("5000.00"), 10));

            List<Product> result = repository.findByPriceBetween(
                    new BigDecimal("100.00"), new BigDecimal("1000.00"));

            assertThat(result)
                    .hasSize(1)
                    .extracting(Product::getName)
                    .containsExactly("Médio");
        }
    }

    // ====================================================================
    // findByNameContainingIgnoreCase
    // ====================================================================
    @Nested
    @DisplayName("findByNameContainingIgnoreCase()")
    class FindByName {

        @Test
        @DisplayName("deve buscar por nome ignorando maiúsculas/minúsculas")
        void shouldSearchByNameIgnoreCase() {
            repository.save(new Product("Notebook Dell", "NTB-001",
                    new BigDecimal("4500.00"), 10));
            repository.save(new Product("Notebook Lenovo", "NTB-002",
                    new BigDecimal("3500.00"), 5));
            repository.save(new Product("Mouse Logitech", "MSE-001",
                    new BigDecimal("150.00"), 50));

            List<Product> result = repository.findByNameContainingIgnoreCase("notebook");

            assertThat(result)
                    .hasSize(2)
                    .extracting(Product::getName)
                    .allMatch(name -> name.toLowerCase().contains("notebook"));
        }
    }

    // ====================================================================
    // Paginação
    // ====================================================================
    @Nested
    @DisplayName("Paginação")
    class Pagination {

        @Test
        @DisplayName("deve paginar produtos em estoque")
        void shouldPaginateInStockProducts() {
            for (int i = 1; i <= 15; i++) {
                repository.save(new Product("Produto " + i, "PRD-" + String.format("%03d", i),
                        new BigDecimal("100.00"), i));
            }

            Page<Product> firstPage = repository.findByQuantityGreaterThan(0,
                    PageRequest.of(0, 5));

            assertThat(firstPage.getContent()).hasSize(5);
            assertThat(firstPage.getTotalElements()).isEqualTo(15);
            assertThat(firstPage.getTotalPages()).isEqualTo(3);
            assertThat(firstPage.isFirst()).isTrue();
            assertThat(firstPage.isLast()).isFalse();
        }
    }
}
