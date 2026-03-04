package com.example.testingdemo.builder;

import com.example.testingdemo.dto.ProductRequest;
import com.example.testingdemo.model.Product;

import java.math.BigDecimal;

/**
 * Data Builder para criação de objetos de teste.
 *
 * <p>Padrão Builder que facilita a criação de instâncias
 * de {@link Product} e {@link ProductRequest} com valores
 * padrão sensatos para testes.</p>
 *
 * <h3>Uso básico:</h3>
 * <pre>{@code
 * Product product = new ProductBuilder().build();
 * Product custom  = new ProductBuilder()
 *                      .withName("Teclado")
 *                      .withPrice(new BigDecimal("199.90"))
 *                      .build();
 * ProductRequest request = new ProductBuilder().buildRequest();
 * }</pre>
 */
public class ProductBuilder {

    private Long id = 1L;
    private String name = "Notebook Dell";
    private String sku = "NTB-001";
    private BigDecimal price = new BigDecimal("4599.90");
    private Integer quantity = 10;

    public ProductBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ProductBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder withSku(String sku) {
        this.sku = sku;
        return this;
    }

    public ProductBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ProductBuilder withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    /**
     * Constrói um {@link Product} (entidade JPA) com id setado via reflection-free setter.
     */
    public Product build() {
        Product product = new Product(name, sku, price, quantity);
        product.setId(id);
        return product;
    }

    /**
     * Constrói um {@link ProductRequest} (record DTO) para chamadas de API/service.
     */
    public ProductRequest buildRequest() {
        return new ProductRequest(name, sku, price, quantity);
    }
}
