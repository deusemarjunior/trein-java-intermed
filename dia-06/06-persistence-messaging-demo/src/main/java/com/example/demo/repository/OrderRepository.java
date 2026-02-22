package com.example.demo.repository;

import com.example.demo.dto.OrderSummary;
import com.example.demo.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * ❌ PROBLEMA N+1: findAll() padrão carrega Orders sem Category.
     * Ao acessar order.getCategory().getName(), Hibernate faz 1 query extra
     * por pedido → N+1 queries no total.
     *
     * Herdado de JpaRepository — NÃO usar para listagem com category!
     */

    // ✅ SOLUÇÃO 1: JOIN FETCH via JPQL
    @Query("SELECT o FROM Order o JOIN FETCH o.category")
    List<Order> findAllWithCategory();

    // ✅ SOLUÇÃO 2: JOIN FETCH com items (cuidado: multiplica linhas)
    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.category JOIN FETCH o.items")
    List<Order> findAllWithCategoryAndItems();

    // ✅ SOLUÇÃO 3: @EntityGraph (declarativo — mesmo efeito do JOIN FETCH)
    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT o FROM Order o")
    List<Order> findAllWithCategoryGraph();

    // ✅ SOLUÇÃO 4: Projeção DTO (Record) — busca apenas os campos necessários
    @Query("""
            SELECT new com.example.demo.dto.OrderSummary(
                o.id, o.customerName, c.name, o.total, o.status
            )
            FROM Order o JOIN o.category c
            """)
    Page<OrderSummary> findAllSummaries(Pageable pageable);

    // ✅ JOIN FETCH + busca por ID
    @Query("SELECT o FROM Order o JOIN FETCH o.category JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(Long id);
}
