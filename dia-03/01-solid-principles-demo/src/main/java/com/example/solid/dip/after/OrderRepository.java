package com.example.solid.dip.after;

/**
 * ✅ APLICANDO DIP
 * 
 * Abstração (interface) no domínio
 * - Alto nível define o contrato
 * - Baixo nível implementa o contrato
 */
public interface OrderRepository {
    Order save(Order order);
    Order findById(Long id);
}
