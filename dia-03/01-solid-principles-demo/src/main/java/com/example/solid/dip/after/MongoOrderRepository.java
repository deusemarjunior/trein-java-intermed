package com.example.solid.dip.after;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * ✅ Implementação alternativa: MongoDB
 * Basta criar uma nova implementação da interface!
 */
@Repository
@Primary  // Use esta implementação por padrão
public class MongoOrderRepository implements OrderRepository {
    
    @Override
    public Order save(Order order) {
        System.out.println("💾 Salvando no MongoDB: " + order.getDescription());
        // Código específico do MongoDB
        return order;
    }
    
    @Override
    public Order findById(Long id) {
        System.out.println("🔍 Buscando no MongoDB: ID " + id);
        // Código específico do MongoDB
        return new Order();
    }
}
