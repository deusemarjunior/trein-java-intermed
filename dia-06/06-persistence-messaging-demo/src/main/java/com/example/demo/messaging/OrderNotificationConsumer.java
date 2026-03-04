package com.example.demo.messaging;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos de pedidos criados.
 *
 * Escuta a fila "order-notifications" e processa cada mensagem.
 * Em um cenário real, aqui enviaria um email, SMS, push notification, etc.
 */
@Component
public class OrderNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderNotificationConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("📥 Evento recebido: Pedido #{} criado", event.orderId());
        log.info("   Cliente: {} ({})", event.customerName(), event.customerEmail());
        log.info("   Categoria: {}", event.categoryName());
        log.info("   Total: R$ {}", event.total());

        // Simula envio de email de confirmação
        sendConfirmationEmail(event);
    }

    private void sendConfirmationEmail(OrderCreatedEvent event) {
        log.info("📧 [SIMULADO] Enviando email de confirmação para {}", event.customerEmail());
        log.info("   Assunto: Pedido #{} recebido com sucesso!", event.orderId());
        log.info("   Corpo: Olá {}, seu pedido de R$ {} foi recebido e está sendo processado.",
                event.customerName(), event.total());
        log.info("✅ Email de confirmação 'enviado' com sucesso!");
    }
}
