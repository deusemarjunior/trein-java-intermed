package com.example.badpractices.controller;

import com.example.badpractices.model.Order;
import com.example.badpractices.model.OrderItem;
import com.example.badpractices.model.Product;
import com.example.badpractices.repository.ProductRepository;
import com.example.badpractices.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ⚠️ GOD CONTROLLER — faz tudo aqui dentro!
 *
 * Más práticas propositais:
 * - ENTITY exposta diretamente na API (sem DTO)
 * - God Method (createOrder > 100 linhas)
 * - Lógica de negócio no controller
 * - Sem tratamento global de erros (@ControllerAdvice)
 * - Sem validação de entrada (@Valid + Bean Validation)
 * - try/catch genérico retornando 500
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ProductRepository productRepository;

    public OrderController(OrderService orderService, ProductRepository productRepository) {
        this.orderService = orderService;
        this.productRepository = productRepository;
    }

    // ❌ MÁ PRÁTICA: Entity exposta diretamente
    @GetMapping
    public ResponseEntity<List<Order>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    // ❌ MÁ PRÁTICA: Entity exposta diretamente + RuntimeException genérica
    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ❌ GOD METHOD — faz TUDO aqui: validação, regras de negócio, cálculos, persistência
    // ❌ Entity como request body (sem DTO)
    // ❌ Sem @Valid
    // ❌ try/catch genérico retornando 500
    @PostMapping
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> requestBody) {
        try {
            // ❌ Variáveis com nomes ruins
            String n = (String) requestBody.get("customerName");
            String e = (String) requestBody.get("customerEmail");
            String r = (String) requestBody.get("shippingRegion");

            // ❌ Validação manual inline — deveria usar @Valid + @NotBlank
            if (n == null || n.trim().isEmpty()) {
                Map<String, String> err = new HashMap<>();
                err.put("error", "Nome do cliente é obrigatório");
                return ResponseEntity.badRequest().body(err);
            }
            if (e == null || e.trim().isEmpty()) {
                Map<String, String> err = new HashMap<>();
                err.put("error", "Email do cliente é obrigatório");
                return ResponseEntity.badRequest().body(err);
            }
            if (!e.contains("@")) {
                Map<String, String> err = new HashMap<>();
                err.put("error", "Email inválido");
                return ResponseEntity.badRequest().body(err);
            }
            if (r == null || r.trim().isEmpty()) {
                Map<String, String> err = new HashMap<>();
                err.put("error", "Região de envio é obrigatória");
                return ResponseEntity.badRequest().body(err);
            }

            // ❌ Lista de regiões válidas hardcoded
            List<String> regioes = List.of("SUDESTE", "SUL", "CENTRO-OESTE", "NORDESTE", "NORTE");
            if (!regioes.contains(r.toUpperCase())) {
                Map<String, String> err = new HashMap<>();
                err.put("error", "Região inválida. Use: SUDESTE, SUL, CENTRO-OESTE, NORDESTE, NORTE");
                return ResponseEntity.badRequest().body(err);
            }

            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) requestBody.get("items");
            if (itemsData == null || itemsData.isEmpty()) {
                Map<String, String> err = new HashMap<>();
                err.put("error", "Pedido deve ter pelo menos um item");
                return ResponseEntity.badRequest().body(err);
            }

            // ❌ Validar cada item no controller — deveria estar no service
            for (Map<String, Object> item : itemsData) {
                if (!item.containsKey("productId")) {
                    Map<String, String> err = new HashMap<>();
                    err.put("error", "productId é obrigatório em cada item");
                    return ResponseEntity.badRequest().body(err);
                }
                if (!item.containsKey("quantity")) {
                    Map<String, String> err = new HashMap<>();
                    err.put("error", "quantity é obrigatório em cada item");
                    return ResponseEntity.badRequest().body(err);
                }

                Integer qty = Integer.valueOf(item.get("quantity").toString());
                if (qty <= 0) {
                    Map<String, String> err = new HashMap<>();
                    err.put("error", "Quantidade deve ser maior que zero");
                    return ResponseEntity.badRequest().body(err);
                }

                Long pid = Long.valueOf(item.get("productId").toString());
                if (!productRepository.existsById(pid)) {
                    Map<String, String> err = new HashMap<>();
                    err.put("error", "Produto não encontrado: " + pid);
                    return ResponseEntity.badRequest().body(err);
                }
            }

            // ❌ Criando entity diretamente no controller
            Order order = new Order();
            order.setCustomerName(n);
            order.setCustomerEmail(e);
            order.setShippingRegion(r.toUpperCase());

            // ❌ Delegando para service que também tem lógica duplicada
            Order saved = orderService.processOrder(order, itemsData);

            // ❌ Construindo resposta manualmente — deveria ser um DTO
            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("customerName", saved.getCustomerName());
            response.put("customerEmail", saved.getCustomerEmail());
            response.put("shippingRegion", saved.getShippingRegion());
            response.put("status", saved.getStatus());
            response.put("total", saved.getTotal());
            response.put("itemCount", saved.getItems().size());
            response.put("createdAt", saved.getCreatedAt());

            return ResponseEntity.status(201).body(response);

        } catch (RuntimeException ex) {
            // ❌ MÁ PRÁTICA: catch genérico retornando 500
            Map<String, String> err = new HashMap<>();
            err.put("error", ex.getMessage());
            return ResponseEntity.status(500).body(err);
        }
    }

    // ❌ ENTITY exposta diretamente na resposta
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(404).body(err);
        }
    }

    // ❌ Retorna texto puro — deveria ser DTO estruturado
    @GetMapping("/{id}/summary")
    public ResponseEntity<String> getOrderSummary(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.getOrderSummary(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ❌ Endpoint utilitário no controller de Orders — violação de SRP
    @GetMapping("/shipping/{region}")
    public ResponseEntity<?> getShippingCost(@PathVariable String region) {
        try {
            BigDecimal cost = orderService.calculateShipping(region);
            Map<String, Object> response = new HashMap<>();
            response.put("region", region);
            response.put("cost", cost);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Erro ao calcular frete");
            return ResponseEntity.status(500).body(err);
        }
    }
}
