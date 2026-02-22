package com.example.demo.messaging;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer de eventos de produtos criados.
 * Simula o processamento de um novo produto (ex: notificação, indexação).
 */
@Component
public class ProductNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductNotificationConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleProductCreated(ProductCreatedEvent event) {
        log.info("Evento recebido: Produto #{} criado", event.productId());
        log.info("  Nome: {} | Categoria: {}", event.name(), event.category());
        log.info("  Preço: R$ {} | Estoque: {}", event.price(), event.stock());

        // Simula processamento (ex: indexação, envio de email)
        log.info("[SIMULADO] Processando novo produto: {}", event.name());
        log.info("Processamento concluído para produto #{}", event.productId());
    }
}
