package com.example.testingdemo.service;

import com.example.testingdemo.builder.ProductBuilder;
import com.example.testingdemo.dto.ProductRequest;
import com.example.testingdemo.dto.ProductResponse;
import com.example.testingdemo.exception.DuplicateSkuException;
import com.example.testingdemo.exception.ProductNotFoundException;
import com.example.testingdemo.model.Product;
import com.example.testingdemo.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do {@link ProductService} usando Mockito.
 *
 * <p>Demonstra:</p>
 * <ul>
 *   <li>@Mock e @InjectMocks</li>
 *   <li>when / thenReturn / thenThrow</li>
 *   <li>verify e ArgumentCaptor</li>
 *   <li>@Nested para agrupar cenários</li>
 *   <li>AssertJ para asserções fluentes</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService — Testes Unitários")
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    // ====================================================================
    // findAll
    // ====================================================================
    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("deve retornar lista de produtos")
        void shouldReturnProductList() {
            // Arrange
            Product p1 = new ProductBuilder().withId(1L).withSku("SKU-1").build();
            Product p2 = new ProductBuilder().withId(2L).withSku("SKU-2").withName("Mouse").build();
            when(repository.findAll()).thenReturn(List.of(p1, p2));

            // Act
            List<ProductResponse> result = service.findAll();

            // Assert
            assertThat(result)
                    .hasSize(2)
                    .extracting(ProductResponse::sku)
                    .containsExactly("SKU-1", "SKU-2");

            verify(repository, times(1)).findAll();
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há produtos")
        void shouldReturnEmptyList() {
            when(repository.findAll()).thenReturn(List.of());

            List<ProductResponse> result = service.findAll();

            assertThat(result).isEmpty();
        }
    }

    // ====================================================================
    // findById
    // ====================================================================
    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("deve retornar produto quando encontrado")
        void shouldReturnProduct() {
            Product product = new ProductBuilder().build();
            when(repository.findById(1L)).thenReturn(Optional.of(product));

            ProductResponse result = service.findById(1L);

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Notebook Dell");
        }

        @Test
        @DisplayName("deve lançar exceção quando produto não encontrado")
        void shouldThrowWhenNotFound() {
            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(999L))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    // ====================================================================
    // create
    // ====================================================================
    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("deve criar produto com sucesso")
        void shouldCreateProduct() {
            // Arrange
            ProductRequest request = new ProductBuilder()
                    .withSku("NEW-001")
                    .withName("Webcam HD")
                    .withPrice(new BigDecimal("259.90"))
                    .withQuantity(20)
                    .buildRequest();

            when(repository.existsBySku("NEW-001")).thenReturn(false);

            Product saved = new ProductBuilder()
                    .withId(10L)
                    .withSku("NEW-001")
                    .withName("Webcam HD")
                    .withPrice(new BigDecimal("259.90"))
                    .withQuantity(20)
                    .build();
            when(repository.save(any(Product.class))).thenReturn(saved);

            // Act
            ProductResponse result = service.create(request);

            // Assert
            assertThat(result.id()).isEqualTo(10L);
            assertThat(result.name()).isEqualTo("Webcam HD");
            assertThat(result.sku()).isEqualTo("NEW-001");

            // Verifica que o repository.save foi chamado com os dados corretos
            verify(repository).save(productCaptor.capture());
            Product captured = productCaptor.getValue();
            assertThat(captured.getName()).isEqualTo("Webcam HD");
            assertThat(captured.getSku()).isEqualTo("NEW-001");
        }

        @Test
        @DisplayName("deve lançar exceção quando SKU já existe")
        void shouldThrowWhenSkuExists() {
            ProductRequest request = new ProductBuilder()
                    .withSku("DUP-001")
                    .buildRequest();
            when(repository.existsBySku("DUP-001")).thenReturn(true);

            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(DuplicateSkuException.class)
                    .hasMessageContaining("DUP-001");

            // Verifica que save NUNCA foi chamado
            verify(repository, never()).save(any());
        }
    }

    // ====================================================================
    // update
    // ====================================================================
    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("deve atualizar produto existente")
        void shouldUpdateProduct() {
            Product existing = new ProductBuilder().withId(1L).withSku("OLD-001").build();
            when(repository.findById(1L)).thenReturn(Optional.of(existing));
            when(repository.findBySku("NEW-001")).thenReturn(Optional.empty());
            when(repository.save(any(Product.class))).thenReturn(existing);

            ProductRequest request = new ProductBuilder()
                    .withSku("NEW-001")
                    .withName("Produto Atualizado")
                    .buildRequest();

            ProductResponse result = service.update(1L, request);

            assertThat(result).isNotNull();
            verify(repository).save(productCaptor.capture());
            assertThat(productCaptor.getValue().getName()).isEqualTo("Produto Atualizado");
        }

        @Test
        @DisplayName("deve lançar exceção ao atualizar produto inexistente")
        void shouldThrowWhenProductNotFound() {
            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            ProductRequest request = new ProductBuilder().buildRequest();

            assertThatThrownBy(() -> service.update(999L, request))
                    .isInstanceOf(ProductNotFoundException.class);
        }
    }

    // ====================================================================
    // delete
    // ====================================================================
    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("deve deletar produto existente")
        void shouldDeleteProduct() {
            when(repository.existsById(1L)).thenReturn(true);

            service.delete(1L);

            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("deve lançar exceção ao deletar produto inexistente")
        void shouldThrowWhenDeleting() {
            when(repository.existsById(anyLong())).thenReturn(false);

            assertThatThrownBy(() -> service.delete(999L))
                    .isInstanceOf(ProductNotFoundException.class);

            verify(repository, never()).deleteById(anyLong());
        }
    }
}
