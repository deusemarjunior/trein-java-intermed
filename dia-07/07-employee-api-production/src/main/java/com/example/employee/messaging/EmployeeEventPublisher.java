package com.example.employee.messaging;

import com.example.employee.config.RabbitMQConfig;
import com.example.employee.dto.EmployeeCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publisher de eventos de funcionários criados.
 */
@Component
public class EmployeeEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EmployeeEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public EmployeeEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("Publicando evento EmployeeCreated para funcionário #{}", event.employeeId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                event
        );

        log.info("Evento publicado com sucesso para funcionário #{}", event.employeeId());
    }
}
