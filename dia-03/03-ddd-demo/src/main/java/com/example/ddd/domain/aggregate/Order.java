package com.example.ddd.domain.aggregate;

import com.example.ddd.domain.valueobject.Address;
import com.example.ddd.domain.valueobject.Email;
import com.example.ddd.domain.valueobject.Money;
import lombok.Getter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AGGREGATE ROOT: Order
 * - Controla acesso aos OrderItems
 * - Mantém invariantes
 */
@Getter
public class Order {
    private final Email customerEmail;
    private final Address shippingAddress;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.PENDING;
    
    public Order(Email customerEmail, Address shippingAddress) {
        this.customerEmail = customerEmail;
        this.shippingAddress = shippingAddress;
    }
    
    // ✅ Acesso controlado - única forma de adicionar items
    public void addItem(OrderItem item) {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Pedido já confirmado");
        }
        items.add(item);
    }
    
    public void confirm() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Pedido vazio");
        }
        this.status = OrderStatus.CONFIRMED;
    }
    
    // ✅ Retorna cópia imutável
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
    
    @Override
    public String toString() {
        return "Order{customer=" + customerEmail + ", items=" + items.size() + ", status=" + status + "}";
    }
}

enum OrderStatus {
    PENDING, CONFIRMED, SHIPPED
}
