package com.example.badpractices.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Testes de integração — devem passar ANTES e DEPOIS da refatoração.
 *
 * Estes testes validam o COMPORTAMENTO da API, não a estrutura interna.
 * Após refatorar, os mesmos endpoints devem retornar os mesmos resultados.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("GET /api/orders")
    class FindAllTests {

        @Test
        @DisplayName("Deve listar pedidos existentes")
        void shouldListOrders() throws Exception {
            mockMvc.perform(get("/api/orders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                    .andExpect(jsonPath("$[0].customerName").exists())
                    .andExpect(jsonPath("$[0].total").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/orders/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar pedido por ID")
        void shouldReturnOrderById() throws Exception {
            mockMvc.perform(get("/api/orders/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.customerName", is("João Silva")))
                    .andExpect(jsonPath("$.customerEmail", is("joao@email.com")))
                    .andExpect(jsonPath("$.status", is("COMPLETED")));
        }

        @Test
        @DisplayName("Deve retornar 404 para pedido inexistente")
        void shouldReturn404ForNotFound() throws Exception {
            mockMvc.perform(get("/api/orders/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/orders")
    class CreateOrderTests {

        @Test
        @DisplayName("Deve criar pedido com sucesso")
        void shouldCreateOrder() throws Exception {
            String json = """
                    {
                      "customerName": "Carlos Teste",
                      "customerEmail": "carlos@email.com",
                      "shippingRegion": "SUDESTE",
                      "items": [
                        {"productId": 1, "quantity": 1},
                        {"productId": 2, "quantity": 2}
                      ]
                    }
                    """;

            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.customerName", is("Carlos Teste")))
                    .andExpect(jsonPath("$.status", is("PENDING")))
                    .andExpect(jsonPath("$.total").isNumber());
        }

        @Test
        @DisplayName("Deve aplicar 5% desconto para 3 itens")
        void shouldApplyFivePercentDiscount() throws Exception {
            String json = """
                    {
                      "customerName": "Desconto Teste",
                      "customerEmail": "desconto@email.com",
                      "shippingRegion": "SUDESTE",
                      "items": [
                        {"productId": 2, "quantity": 3}
                      ]
                    }
                    """;

            // Mouse 150 * 3 * 0.95 = 427.50 + frete 15 = 442.50
            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.total", is(442.50)));
        }

        @Test
        @DisplayName("Deve retornar frete grátis acima de R$ 500")
        void shouldApplyFreeShipping() throws Exception {
            String json = """
                    {
                      "customerName": "Frete Grátis",
                      "customerEmail": "frete@email.com",
                      "shippingRegion": "NORTE",
                      "items": [
                        {"productId": 1, "quantity": 1}
                      ]
                    }
                    """;

            // Notebook 4500 > 500 -> frete grátis -> total = 4500.00
            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.total", is(4500.00)));
        }

        @Test
        @DisplayName("Deve rejeitar pedido sem nome do cliente")
        void shouldRejectWithoutCustomerName() throws Exception {
            String json = """
                    {
                      "customerEmail": "sem.nome@email.com",
                      "shippingRegion": "SUDESTE",
                      "items": [
                        {"productId": 1, "quantity": 1}
                      ]
                    }
                    """;

            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        @DisplayName("Deve rejeitar pedido sem email")
        void shouldRejectWithoutEmail() throws Exception {
            String json = """
                    {
                      "customerName": "Sem Email",
                      "shippingRegion": "SUDESTE",
                      "items": [
                        {"productId": 1, "quantity": 1}
                      ]
                    }
                    """;

            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        @DisplayName("Deve rejeitar pedido com email inválido")
        void shouldRejectWithInvalidEmail() throws Exception {
            String json = """
                    {
                      "customerName": "Email Inválido",
                      "customerEmail": "nao-e-email",
                      "shippingRegion": "SUDESTE",
                      "items": [
                        {"productId": 1, "quantity": 1}
                      ]
                    }
                    """;

            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        @DisplayName("Deve rejeitar pedido sem itens")
        void shouldRejectWithoutItems() throws Exception {
            String json = """
                    {
                      "customerName": "Sem Itens",
                      "customerEmail": "sem.itens@email.com",
                      "shippingRegion": "SUDESTE",
                      "items": []
                    }
                    """;

            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        @DisplayName("Deve rejeitar pedido com região inválida")
        void shouldRejectWithInvalidRegion() throws Exception {
            String json = """
                    {
                      "customerName": "Região Inv",
                      "customerEmail": "regiao@email.com",
                      "shippingRegion": "MARTE",
                      "items": [
                        {"productId": 1, "quantity": 1}
                      ]
                    }
                    """;

            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        @DisplayName("Deve rejeitar pedido com quantidade zero")
        void shouldRejectWithZeroQuantity() throws Exception {
            String json = """
                    {
                      "customerName": "Qty Zero",
                      "customerEmail": "qty@email.com",
                      "shippingRegion": "SUL",
                      "items": [
                        {"productId": 1, "quantity": 0}
                      ]
                    }
                    """;

            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        @DisplayName("Deve rejeitar pedido com produto inexistente")
        void shouldRejectWithNonExistentProduct() throws Exception {
            String json = """
                    {
                      "customerName": "Prod Inex",
                      "customerEmail": "prod@email.com",
                      "shippingRegion": "SUL",
                      "items": [
                        {"productId": 999, "quantity": 1}
                      ]
                    }
                    """;

            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    @DisplayName("DELETE /api/orders/{id}")
    class DeleteOrderTests {

        @Test
        @DisplayName("Deve deletar pedido existente")
        void shouldDeleteOrder() throws Exception {
            // Primeiro cria um pedido
            String json = """
                    {
                      "customerName": "Para Deletar",
                      "customerEmail": "deletar@email.com",
                      "shippingRegion": "SUL",
                      "items": [
                        {"productId": 2, "quantity": 1}
                      ]
                    }
                    """;

            String response = mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            // Extrai o ID da resposta (formato JSON com campo "id")
            String idStr = response.split("\"id\":")[1].split("[,}]")[0].trim();

            mockMvc.perform(delete("/api/orders/" + idStr))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar 404 ao deletar pedido inexistente")
        void shouldReturn404WhenDeletingNonExistent() throws Exception {
            mockMvc.perform(delete("/api/orders/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/orders/shipping/{region}")
    class ShippingCostTests {

        @Test
        @DisplayName("Deve retornar custo de frete para SUDESTE")
        void shouldReturnShippingForSudeste() throws Exception {
            mockMvc.perform(get("/api/orders/shipping/SUDESTE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.region", is("SUDESTE")))
                    .andExpect(jsonPath("$.cost", is(15.0)));
        }

        @Test
        @DisplayName("Deve retornar custo de frete para NORTE")
        void shouldReturnShippingForNorte() throws Exception {
            mockMvc.perform(get("/api/orders/shipping/NORTE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.region", is("NORTE")))
                    .andExpect(jsonPath("$.cost", is(40.0)));
        }
    }
}
