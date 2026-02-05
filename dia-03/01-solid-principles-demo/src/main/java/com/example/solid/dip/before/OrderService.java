package com.example.solid.dip.before;

/**
 * ❌ VIOLAÇÃO DO DIP (Dependency Inversion Principle)
 * 
 * Problemas:
 * 1. Classe de alto nível (OrderService) depende de classe de baixo nível (MySQLOrderRepository)
 * 2. Acoplamento direto com implementação concreta
 * 3. Impossível trocar MySQL por outro banco sem modificar esta classe
 * 4. Difícil testar (não pode mockar MySQLOrderRepository)
 */
public class OrderService {
    
    // ❌ Dependência direta de implementação concreta
    private final MySQLOrderRepository repository;
    
    public OrderService() {
        // ❌ Instancia diretamente a implementação
        this.repository = new MySQLOrderRepository();
    }
    
    public Order createOrder(String description, Double total) {
        Order order = new Order();
        order.setDescription(description);
        order.setTotal(total);
        
        // Lógica de negócio depende de detalhe de infraestrutura
        return repository.save(order);
    }
    
    public Order getOrder(Long id) {
        return repository.findById(id);
    }
}
