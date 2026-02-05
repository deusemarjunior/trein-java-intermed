package com.example.ddd.domain.aggregate;

import com.example.ddd.domain.valueobject.Money;
import lombok.Getter;

/**
 * ENTITY dentro do Aggregate Order
 */
@Getter
public class OrderItem {
    private final String productName;
    private final Money unitPrice;
    private final int quantity;
    
    public OrderItem(String productName, Money unitPrice, int quantity) {
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }
    
    public Money getTotal() {
        return unitPrice.multiply(quantity);
    }
    
    @Override
    public String toString() {
        return productName + " x" + quantity + " = " + getTotal();
    }
}
