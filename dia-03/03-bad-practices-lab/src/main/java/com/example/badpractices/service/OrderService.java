package com.example.badpractices.service;

import com.example.badpractices.model.Order;
import com.example.badpractices.model.OrderItem;
import com.example.badpractices.model.Product;
import com.example.badpractices.repository.OrderRepository;
import com.example.badpractices.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * ⚠️ GOD CLASS — Esta classe faz TUDO!
 *
 * Más práticas propositais:
 * - Nomes de variáveis sem significado (x, temp, d, res, val)
 * - Números mágicos (0.1, 0.15, 0.05, 50.0, 30.0, etc.)
 * - Código duplicado (cálculo de desconto aparece 2 vezes)
 * - try/catch genérico (catch Exception)
 * - Cadeia de if/else para cálculo de frete
 * - Responsabilidades misturadas (validação + negócio + persistência)
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    // ❌ MÁ PRÁTICA: Nomes de variáveis sem significado
    // ❌ MÁ PRÁTICA: Método faz validação + cálculo + persistência
    // ❌ MÁ PRÁTICA: Números mágicos por toda parte
    public Order processOrder(Order order, List<Map<String, Object>> items) {
        try {
            // ❌ Variável "x" — o que é isso?
            var x = order.getCustomerName();
            if (x == null || x.trim().isEmpty()) {
                throw new RuntimeException("Nome do cliente é obrigatório");
            }
            // ❌ Variável "temp" — temporário de quê?
            var temp = order.getCustomerEmail();
            if (temp == null || !temp.contains("@")) {
                throw new RuntimeException("Email inválido");
            }

            // ❌ Variável "d" — d de quê?
            var d = order.getShippingRegion();
            if (d == null || d.trim().isEmpty()) {
                throw new RuntimeException("Região de envio é obrigatória");
            }

            // Processar itens
            BigDecimal res = BigDecimal.ZERO; // ❌ "res" — resultado? reserva?

            for (Map<String, Object> item : items) {
                // ❌ Variável "val" — valor? validação?
                Long val = Long.valueOf(item.get("productId").toString());
                Integer qty = Integer.valueOf(item.get("quantity").toString());

                if (qty <= 0) {
                    throw new RuntimeException("Quantidade deve ser maior que zero");
                }

                Product p = productRepository.findById(val)
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + val));

                OrderItem oi = new OrderItem();
                oi.setProduct(p);
                oi.setQuantity(qty);
                oi.setUnitPrice(p.getPrice());

                // ❌ CÓDIGO DUPLICADO #1 — cálculo de desconto por quantidade
                BigDecimal sub;
                if (qty >= 10) {
                    sub = p.getPrice().multiply(BigDecimal.valueOf(qty))
                            .multiply(BigDecimal.valueOf(0.85)); // ❌ Número mágico: 15% desconto
                } else if (qty >= 5) {
                    sub = p.getPrice().multiply(BigDecimal.valueOf(qty))
                            .multiply(BigDecimal.valueOf(0.9)); // ❌ Número mágico: 10% desconto
                } else if (qty >= 3) {
                    sub = p.getPrice().multiply(BigDecimal.valueOf(qty))
                            .multiply(BigDecimal.valueOf(0.95)); // ❌ Número mágico: 5% desconto
                } else {
                    sub = p.getPrice().multiply(BigDecimal.valueOf(qty));
                }
                sub = sub.setScale(2, RoundingMode.HALF_UP);

                oi.setSubtotal(sub);
                order.addItem(oi);
                res = res.add(sub);
            }

            if (order.getItems().isEmpty()) {
                throw new RuntimeException("Pedido deve ter pelo menos um item");
            }

            // ❌ CADEIA DE IF/ELSE — cálculo de frete por região
            BigDecimal frete;
            if (d.equalsIgnoreCase("SUDESTE")) {
                frete = BigDecimal.valueOf(15.0); // ❌ Número mágico
            } else if (d.equalsIgnoreCase("SUL")) {
                frete = BigDecimal.valueOf(20.0); // ❌ Número mágico
            } else if (d.equalsIgnoreCase("CENTRO-OESTE")) {
                frete = BigDecimal.valueOf(25.0); // ❌ Número mágico
            } else if (d.equalsIgnoreCase("NORDESTE")) {
                frete = BigDecimal.valueOf(30.0); // ❌ Número mágico
            } else if (d.equalsIgnoreCase("NORTE")) {
                frete = BigDecimal.valueOf(40.0); // ❌ Número mágico
            } else {
                frete = BigDecimal.valueOf(50.0); // ❌ Número mágico
            }

            // ❌ Número mágico: frete grátis acima de 500
            if (res.compareTo(BigDecimal.valueOf(500.0)) > 0) {
                frete = BigDecimal.ZERO;
            }

            res = res.add(frete);
            order.setTotal(res.setScale(2, RoundingMode.HALF_UP));
            order.setStatus("PENDING");

            return orderRepository.save(order);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            // ❌ MÁ PRÁTICA: catch genérico — esconde o erro real
            throw new RuntimeException("Erro ao processar pedido: " + e.getMessage());
        }
    }

    // ❌ CÓDIGO DUPLICADO #2 — mesmo cálculo de desconto que processOrder()
    public BigDecimal calculateItemDiscount(BigDecimal price, int qty) {
        BigDecimal sub;
        if (qty >= 10) {
            sub = price.multiply(BigDecimal.valueOf(qty))
                    .multiply(BigDecimal.valueOf(0.85)); // ❌ Mesmo número mágico duplicado
        } else if (qty >= 5) {
            sub = price.multiply(BigDecimal.valueOf(qty))
                    .multiply(BigDecimal.valueOf(0.9));  // ❌ Duplicado
        } else if (qty >= 3) {
            sub = price.multiply(BigDecimal.valueOf(qty))
                    .multiply(BigDecimal.valueOf(0.95)); // ❌ Duplicado
        } else {
            sub = price.multiply(BigDecimal.valueOf(qty));
        }
        return sub.setScale(2, RoundingMode.HALF_UP);
    }

    // ❌ MÁ PRÁTICA: Cálculo de frete duplicado (mesma lógica do processOrder)
    public BigDecimal calculateShipping(String region) {
        BigDecimal frete;
        if (region.equalsIgnoreCase("SUDESTE")) {
            frete = BigDecimal.valueOf(15.0);
        } else if (region.equalsIgnoreCase("SUL")) {
            frete = BigDecimal.valueOf(20.0);
        } else if (region.equalsIgnoreCase("CENTRO-OESTE")) {
            frete = BigDecimal.valueOf(25.0);
        } else if (region.equalsIgnoreCase("NORDESTE")) {
            frete = BigDecimal.valueOf(30.0);
        } else if (region.equalsIgnoreCase("NORTE")) {
            frete = BigDecimal.valueOf(40.0);
        } else {
            frete = BigDecimal.valueOf(50.0);
        }
        return frete;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        // ❌ MÁ PRÁTICA: RuntimeException genérica
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + id));
    }

    public void deleteOrder(Long id) {
        // ❌ MÁ PRÁTICA: RuntimeException genérica
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Pedido não encontrado com id: " + id);
        }
        orderRepository.deleteById(id);
    }

    // ❌ MÁ PRÁTICA: Método com responsabilidade confusa — calcula resumo E formata
    public String getOrderSummary(Long id) {
        Order o = findById(id);
        var sb = new StringBuilder();
        sb.append("Pedido #").append(o.getId()).append("\n");
        sb.append("Cliente: ").append(o.getCustomerName()).append("\n");
        sb.append("Email: ").append(o.getCustomerEmail()).append("\n");
        sb.append("Status: ").append(o.getStatus()).append("\n");
        sb.append("Região: ").append(o.getShippingRegion()).append("\n");
        sb.append("Itens:\n");
        for (OrderItem i : o.getItems()) {
            sb.append("  - ").append(i.getProduct().getName())
              .append(" x").append(i.getQuantity())
              .append(" = R$ ").append(i.getSubtotal()).append("\n");
        }
        sb.append("Total: R$ ").append(o.getTotal()).append("\n");
        return sb.toString();
    }
}
