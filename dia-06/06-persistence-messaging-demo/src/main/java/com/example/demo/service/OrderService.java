package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.messaging.OrderEventPublisher;
import com.example.demo.model.Category;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
                        CategoryRepository categoryRepository,
                        OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
    }

    // ────────────────────────────── N+1 DEMOS ──────────────────────────────

    /**
     * ❌ DEMONSTRA o problema N+1.
     * findAll() carrega Orders sem Category. Ao montar o response,
     * order.getCategory() dispara 1 query adicional por pedido.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> findAllWithNPlusOne() {
        log.warn("⚠️ Executando findAll() SEM JOIN FETCH — observe as queries N+1 no log!");
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(OrderResponse::from).toList();
    }

    /**
     * ✅ Resolve N+1 com JOIN FETCH via JPQL.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> findAllOptimized() {
        log.info("✅ Executando findAllWithCategoryAndItems() com JOIN FETCH");
        List<Order> orders = orderRepository.findAllWithCategoryAndItems();
        return orders.stream().map(OrderResponse::from).toList();
    }

    /**
     * ✅ Resolve N+1 com @EntityGraph.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> findAllWithEntityGraph() {
        log.info("✅ Executando findAllWithCategoryGraph() com @EntityGraph");
        List<Order> orders = orderRepository.findAllWithCategoryGraph();
        // Nota: items ainda são LAZY aqui — apenas category é carregada
        return orders.stream().map(o -> new OrderResponse(
                o.getId(), o.getCustomerName(), o.getCustomerEmail(),
                o.getCategory().getName(), o.getStatus(), o.getTotal(),
                List.of(), o.getCreatedAt()
        )).toList();
    }

    // ────────────────────────────── PROJEÇÃO DTO ──────────────────────────────

    /**
     * ✅ Projeção DTO com Pageable — busca apenas os campos necessários.
     * Ideal para listagens e tabelas.
     */
    @Transactional(readOnly = true)
    public Page<OrderSummary> findSummaries(Pageable pageable) {
        log.info("✅ Executando findAllSummaries() com projeção DTO + paginação");
        return orderRepository.findAllSummaries(pageable);
    }

    // ────────────────────────────── CRUD ──────────────────────────────

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));

        Order order = new Order();
        order.setCustomerName(request.customerName());
        order.setCustomerEmail(request.customerEmail());
        order.setCategory(category);

        for (OrderRequest.ItemRequest itemReq : request.items()) {
            OrderItem item = new OrderItem(
                    itemReq.productName(),
                    itemReq.quantity(),
                    itemReq.unitPrice()
            );
            order.addItem(item);
        }

        Order saved = orderRepository.save(order);
        log.info("📦 Pedido #{} criado com {} itens, total: R$ {}",
                saved.getId(), saved.getItems().size(), saved.getTotal());

        // Publica evento no RabbitMQ
        OrderCreatedEvent event = new OrderCreatedEvent(
                saved.getId(),
                saved.getCustomerName(),
                saved.getCustomerEmail(),
                category.getName(),
                saved.getTotal(),
                LocalDateTime.now()
        );
        eventPublisher.publishOrderCreated(event);

        return OrderResponse.from(saved);
    }
}
