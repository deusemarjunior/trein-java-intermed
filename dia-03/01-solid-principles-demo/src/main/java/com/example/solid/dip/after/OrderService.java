package com.example.solid.dip.after;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ✅ APLICANDO DIP
 * 
 * Vantagens:
 * 1. Depende de abstração (OrderRepository), não de implementação
 * 2. Fácil trocar implementação sem modificar esta classe
 * 3. Fácil testar (pode usar mock da interface)
 * 4. Inversão de controle via Spring (@RequiredArgsConstructor)
 */
@Service
@RequiredArgsConstructor
public class OrderService {
    
    // ✅ Depende de abstração
    private final OrderRepository repository;
    
    // ✅ Injeção de dependência via construtor (Spring)
    // Não precisa instanciar nada!
    
    public Order createOrder(String description, Double total) {
        Order order = new Order();
        order.setDescription(description);
        order.setTotal(total);
        
        // Lógica de negócio independente de detalhes de infraestrutura
        return repository.save(order);
    }
    
    public Order getOrder(Long id) {
        return repository.findById(id);
    }
}
