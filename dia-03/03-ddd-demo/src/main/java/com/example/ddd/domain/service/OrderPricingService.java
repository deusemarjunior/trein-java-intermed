package com.example.ddd.domain.service;

import com.example.ddd.domain.aggregate.Order;
import com.example.ddd.domain.valueobject.Money;
import org.springframework.stereotype.Service;

/**
 * DOMAIN SERVICE
 * - Lógica de negócio que não pertence a uma entidade
 * - Sem estado
 */
@Service
public class OrderPricingService {
    
    public Money calculateTotal(Order order) {
        return order.getItems().stream()
                .map(item -> item.getTotal())
                .reduce(new Money("0", "BRL"), Money::add);
    }
    
    public Money calculateShipping(Order order) {
        String state = order.getShippingAddress().getState();
        return switch (state) {
            case "SP" -> new Money("15.00", "BRL");
            case "RJ" -> new Money("20.00", "BRL");
            default -> new Money("25.00", "BRL");
        };
    }
}
