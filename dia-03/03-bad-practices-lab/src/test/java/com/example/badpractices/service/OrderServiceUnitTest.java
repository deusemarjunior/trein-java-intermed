package com.example.badpractices.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes do OrderService — devem passar ANTES e DEPOIS da refatoração.
 */
class OrderServiceUnitTest {

    private final OrderService orderService;

    OrderServiceUnitTest() {
        // Instancia sem repositórios para testar métodos puros
        this.orderService = new OrderService(null, null);
    }

    @Nested
    @DisplayName("Cálculo de Desconto por Quantidade")
    class DiscountTests {

        @Test
        @DisplayName("Sem desconto para quantidade < 3")
        void noDiscountForSmallQuantity() {
            BigDecimal price = new BigDecimal("100.00");
            BigDecimal result = orderService.calculateItemDiscount(price, 2);
            assertEquals(new BigDecimal("200.00"), result);
        }

        @Test
        @DisplayName("Sem desconto para quantidade = 1")
        void noDiscountForSingleItem() {
            BigDecimal price = new BigDecimal("50.00");
            BigDecimal result = orderService.calculateItemDiscount(price, 1);
            assertEquals(new BigDecimal("50.00"), result);
        }

        @Test
        @DisplayName("5% desconto para quantidade >= 3")
        void fivePercentDiscountForThreeOrMore() {
            BigDecimal price = new BigDecimal("100.00");
            BigDecimal result = orderService.calculateItemDiscount(price, 3);
            // 100 * 3 * 0.95 = 285.00
            assertEquals(new BigDecimal("285.00"), result);
        }

        @Test
        @DisplayName("5% desconto para quantidade = 4")
        void fivePercentDiscountForFour() {
            BigDecimal price = new BigDecimal("100.00");
            BigDecimal result = orderService.calculateItemDiscount(price, 4);
            // 100 * 4 * 0.95 = 380.00
            assertEquals(new BigDecimal("380.00"), result);
        }

        @Test
        @DisplayName("10% desconto para quantidade >= 5")
        void tenPercentDiscountForFiveOrMore() {
            BigDecimal price = new BigDecimal("100.00");
            BigDecimal result = orderService.calculateItemDiscount(price, 5);
            // 100 * 5 * 0.90 = 450.00
            assertEquals(new BigDecimal("450.00"), result);
        }

        @Test
        @DisplayName("10% desconto para quantidade = 9")
        void tenPercentDiscountForNine() {
            BigDecimal price = new BigDecimal("100.00");
            BigDecimal result = orderService.calculateItemDiscount(price, 9);
            // 100 * 9 * 0.90 = 810.00
            assertEquals(new BigDecimal("810.00"), result);
        }

        @Test
        @DisplayName("15% desconto para quantidade >= 10")
        void fifteenPercentDiscountForTenOrMore() {
            BigDecimal price = new BigDecimal("100.00");
            BigDecimal result = orderService.calculateItemDiscount(price, 10);
            // 100 * 10 * 0.85 = 850.00
            assertEquals(new BigDecimal("850.00"), result);
        }

        @Test
        @DisplayName("15% desconto para grande quantidade")
        void fifteenPercentDiscountForLargeQuantity() {
            BigDecimal price = new BigDecimal("50.00");
            BigDecimal result = orderService.calculateItemDiscount(price, 20);
            // 50 * 20 * 0.85 = 850.00
            assertEquals(new BigDecimal("850.00"), result);
        }

        @Test
        @DisplayName("Desconto com preço com centavos")
        void discountWithCents() {
            BigDecimal price = new BigDecimal("29.99");
            BigDecimal result = orderService.calculateItemDiscount(price, 5);
            // 29.99 * 5 * 0.90 = 134.955 -> 134.96 (HALF_UP)
            assertEquals(new BigDecimal("134.96"), result);
        }
    }

    @Nested
    @DisplayName("Cálculo de Frete por Região")
    class ShippingTests {

        @Test
        @DisplayName("Frete Sudeste = R$ 15.00")
        void shippingSudeste() {
            BigDecimal result = orderService.calculateShipping("SUDESTE");
            assertEquals(new BigDecimal("15.0"), result);
        }

        @Test
        @DisplayName("Frete Sul = R$ 20.00")
        void shippingSul() {
            BigDecimal result = orderService.calculateShipping("SUL");
            assertEquals(new BigDecimal("20.0"), result);
        }

        @Test
        @DisplayName("Frete Centro-Oeste = R$ 25.00")
        void shippingCentroOeste() {
            BigDecimal result = orderService.calculateShipping("CENTRO-OESTE");
            assertEquals(new BigDecimal("25.0"), result);
        }

        @Test
        @DisplayName("Frete Nordeste = R$ 30.00")
        void shippingNordeste() {
            BigDecimal result = orderService.calculateShipping("NORDESTE");
            assertEquals(new BigDecimal("30.0"), result);
        }

        @Test
        @DisplayName("Frete Norte = R$ 40.00")
        void shippingNorte() {
            BigDecimal result = orderService.calculateShipping("NORTE");
            assertEquals(new BigDecimal("40.0"), result);
        }

        @Test
        @DisplayName("Frete região desconhecida = R$ 50.00")
        void shippingUnknownRegion() {
            BigDecimal result = orderService.calculateShipping("EXTERIOR");
            assertEquals(new BigDecimal("50.0"), result);
        }

        @Test
        @DisplayName("Frete case insensitive")
        void shippingCaseInsensitive() {
            BigDecimal result = orderService.calculateShipping("sudeste");
            assertEquals(new BigDecimal("15.0"), result);
        }
    }
}
