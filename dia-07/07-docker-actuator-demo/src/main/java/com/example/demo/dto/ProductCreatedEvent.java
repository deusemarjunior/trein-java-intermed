package com.example.demo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento publicado no RabbitMQ quando um novo produto é criado.
 */
public record ProductCreatedEvent(
        Long productId,
        String name,
        String category,
        BigDecimal price,
        Integer stock,
        LocalDateTime createdAt
) implements Serializable {}
