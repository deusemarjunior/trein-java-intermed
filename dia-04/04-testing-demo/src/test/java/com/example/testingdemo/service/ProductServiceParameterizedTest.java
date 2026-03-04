package com.example.testingdemo.service;

import com.example.testingdemo.builder.ProductBuilder;
import com.example.testingdemo.model.Product;
import com.example.testingdemo.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Testes parametrizados do {@link ProductService}.
 *
 * <p>Demonstra o uso de {@code @ParameterizedTest} com
 * {@code @CsvSource} e {@code @ValueSource} para executar
 * o mesmo teste com múltiplos conjuntos de dados.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService — Testes Parametrizados")
class ProductServiceParameterizedTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @ParameterizedTest(name = "SKU {0} → nome={1}, preço={2}")
    @DisplayName("deve encontrar produto por SKU com dados variados")
    @CsvSource({
            "NTB-001, Notebook Dell,   4599.90",
            "MSE-002, Mouse Logitech,  149.90",
            "KBD-003, Teclado Mecânico, 399.90",
            "MNT-004, Monitor 27,      1899.90",
            "HST-005, Headset Gamer,    299.90"
    })
    void shouldFindProductBySku(String sku, String name, BigDecimal price) {
        // Arrange
        Product product = new ProductBuilder()
                .withSku(sku)
                .withName(name)
                .withPrice(price)
                .build();
        when(repository.findBySku(sku)).thenReturn(Optional.of(product));

        // Act
        var result = service.findBySku(sku);

        // Assert
        assertThat(result.sku()).isEqualTo(sku);
        assertThat(result.name()).isEqualTo(name);
        assertThat(result.price()).isEqualByComparingTo(price);
    }

    @ParameterizedTest(name = "preço={0} deve ser aceito")
    @DisplayName("deve aceitar preços válidos na criação")
    @CsvSource({
            "0.01",
            "1.00",
            "99.99",
            "9999.99",
            "100000.00"
    })
    void shouldAcceptValidPrices(BigDecimal validPrice) {
        // Arrange
        var request = new ProductBuilder()
                .withPrice(validPrice)
                .withSku("PRICE-" + validPrice)
                .buildRequest();

        Product saved = new ProductBuilder()
                .withPrice(validPrice)
                .withSku("PRICE-" + validPrice)
                .build();

        when(repository.existsBySku(request.sku())).thenReturn(false);
        when(repository.save(org.mockito.ArgumentMatchers.any(Product.class))).thenReturn(saved);

        // Act
        var result = service.create(request);

        // Assert
        assertThat(result.price()).isEqualByComparingTo(validPrice);
    }

    @ParameterizedTest(name = "SKU \"{0}\" não deve existir")
    @DisplayName("deve lançar exceção para SKUs inexistentes")
    @ValueSource(strings = {"INVALID-001", "XXX-999", "NOPE-000"})
    void shouldThrowForInvalidSkus(String invalidSku) {
        when(repository.findBySku(invalidSku)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findBySku(invalidSku))
                .hasMessageContaining(invalidSku);
    }
}
