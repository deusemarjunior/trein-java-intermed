package com.example.ddd.domain.repository;

import com.example.ddd.domain.aggregate.Order;

/**
 * REPOSITORY INTERFACE (domínio)
 * - Define contrato
 * - Implementação fica na infraestrutura
 */
public interface OrderRepository {
    Order save(Order order);
    Order findById(Long id);
}
