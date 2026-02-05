package com.example.solid.dip.before;

/**
 * ❌ Implementação concreta - detalhes de infraestrutura
 */
public class MySQLOrderRepository {
    
    public Order save(Order order) {
        System.out.println("💾 Salvando no MySQL: " + order.getDescription());
        // Código específico do MySQL
        return order;
    }
    
    public Order findById(Long id) {
        System.out.println("🔍 Buscando no MySQL: ID " + id);
        // Código específico do MySQL
        return new Order();
    }
}
