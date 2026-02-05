package com.example.solid.dip.after;

import org.springframework.stereotype.Repository;

/**
 * ✅ Implementação concreta: MySQL
 * Implementa a abstração definida pelo domínio
 */
@Repository
public class MySQLOrderRepository implements OrderRepository {
    
    @Override
    public Order save(Order order) {
        System.out.println("💾 Salvando no MySQL: " + order.getDescription());
        // Código específico do MySQL
        return order;
    }
    
    @Override
    public Order findById(Long id) {
        System.out.println("🔍 Buscando no MySQL: ID " + id);
        // Código específico do MySQL
        return new Order();
    }
}
