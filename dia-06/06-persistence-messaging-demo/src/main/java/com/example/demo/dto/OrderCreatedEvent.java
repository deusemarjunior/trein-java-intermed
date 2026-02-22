package com.example.demo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento publicado no RabbitMQ quando um novo pedido é criado.
 * Implementa Serializable para serialização JSON.
 */
public record OrderCreatedEvent(
        Long orderId,
        String customerName,
        String customerEmail,
        String categoryName,
        BigDecimal total,
        LocalDateTime createdAt
) implements Serializable {}
