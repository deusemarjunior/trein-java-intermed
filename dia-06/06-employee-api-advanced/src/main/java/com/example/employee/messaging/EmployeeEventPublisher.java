package com.example.employee.messaging;

import com.example.employee.config.RabbitMQConfig;
import com.example.employee.dto.EmployeeCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * TODO 6: Publisher de eventos de funcionários criados.
 *
 * Esta classe deve usar RabbitTemplate para publicar um EmployeeCreatedEvent
 * no exchange "employee-exchange" com routing key "employee.created".
 *
 * Passos:
 * 1. Descomente os beans no RabbitMQConfig (Exchange, Queue, Binding)
 * 2. Implemente o método publishEmployeeCreated() abaixo
 * 3. Chame este método no EmployeeService.create() após salvar o funcionário
 */
@Component
public class EmployeeEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EmployeeEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public EmployeeEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * TODO 6d: Implemente a publicação do evento.
     *
     * Use:
     *   rabbitTemplate.convertAndSend(
     *       RabbitMQConfig.EXCHANGE_NAME,
     *       RabbitMQConfig.ROUTING_KEY,
     *       event
     *   );
     */
    public void publishEmployeeCreated(EmployeeCreatedEvent event) {
        // TODO 6d: Descomente as linhas abaixo para publicar o evento

        // log.info("📤 Publicando evento EmployeeCreated para funcionário #{}", event.employeeId());
        //
        // rabbitTemplate.convertAndSend(
        //         RabbitMQConfig.EXCHANGE_NAME,
        //         RabbitMQConfig.ROUTING_KEY,
        //         event
        // );
        //
        // log.info("✅ Evento publicado com sucesso para funcionário #{}", event.employeeId());

        log.warn("⚠️ Publicação de evento ainda não implementada (TODO 6)");
    }
}
