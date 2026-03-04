package com.example.demo.controller;

import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.OrderSummary;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ──────────── Demonstração N+1 ────────────

    /**
     * ❌ Endpoint que demonstra o problema N+1.
     * Observe nos logs: 1 query para orders + N queries para categories.
     */
    @GetMapping("/n-plus-one")
    public ResponseEntity<List<OrderResponse>> findAllNPlusOne() {
        return ResponseEntity.ok(orderService.findAllWithNPlusOne());
    }

    /**
     * ✅ Endpoint otimizado com JOIN FETCH.
     * Observe nos logs: apenas 1 query com JOIN.
     */
    @GetMapping("/optimized")
    public ResponseEntity<List<OrderResponse>> findAllOptimized() {
        return ResponseEntity.ok(orderService.findAllOptimized());
    }

    /**
     * ✅ Endpoint otimizado com @EntityGraph.
     */
    @GetMapping("/entity-graph")
    public ResponseEntity<List<OrderResponse>> findAllEntityGraph() {
        return ResponseEntity.ok(orderService.findAllWithEntityGraph());
    }

    // ──────────── Projeção DTO + Paginação ────────────

    /**
     * ✅ Projeção DTO com paginação.
     * GET /api/orders/summary?page=0&size=5&sort=customerName,asc
     */
    @GetMapping("/summary")
    public ResponseEntity<Page<OrderSummary>> findSummaries(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(orderService.findSummaries(pageable));
    }

    // ──────────── CRUD ────────────

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
