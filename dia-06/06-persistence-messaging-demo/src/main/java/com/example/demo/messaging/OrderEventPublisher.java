package com.example.demo.messaging;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publica eventos de pedidos criados no RabbitMQ.
 *
 * Usa RabbitTemplate para enviar mensagem ao exchange com routing key.
 * A mensagem é serializada como JSON via Jackson2JsonMessageConverter.
 */
@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("📤 Publicando evento OrderCreated para pedido #{}", event.orderId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                event
        );

        log.info("✅ Evento publicado com sucesso para pedido #{}", event.orderId());
    }
}
