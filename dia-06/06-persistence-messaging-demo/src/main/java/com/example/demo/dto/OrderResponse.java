package com.example.demo.dto;

import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de saída para pedidos.
 */
public record OrderResponse(
        Long id,
        String customerName,
        String customerEmail,
        String categoryName,
        String status,
        BigDecimal total,
        List<ItemResponse> items,
        LocalDateTime createdAt
) {
    public record ItemResponse(
            Long id,
            String productName,
            Integer quantity,
            BigDecimal unitPrice
    ) {
        public static ItemResponse from(OrderItem item) {
            return new ItemResponse(
                    item.getId(),
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPrice()
            );
        }
    }

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getCategory().getName(),
                order.getStatus(),
                order.getTotal(),
                order.getItems().stream().map(ItemResponse::from).toList(),
                order.getCreatedAt()
        );
    }
}
