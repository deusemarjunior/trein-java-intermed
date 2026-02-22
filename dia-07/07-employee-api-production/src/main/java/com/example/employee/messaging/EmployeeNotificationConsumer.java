package com.example.employee.messaging;

import com.example.employee.config.RabbitMQConfig;
import com.example.employee.dto.EmployeeCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer de eventos de funcionários criados.
 * Simula o processamento de boas-vindas para novos funcionários.
 */
@Component
public class EmployeeNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmployeeNotificationConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("Evento recebido: Funcionário #{} criado", event.employeeId());
        log.info("  Nome: {} ({})", event.name(), event.email());
        log.info("  Departamento: {}", event.departmentName());
        log.info("  Salário: R$ {}", event.salary());

        // Simula envio de email de boas-vindas
        log.info("[SIMULADO] Enviando email de boas-vindas para {}", event.email());
        log.info("  Assunto: Bem-vindo(a) à empresa, {}!", event.name());
        log.info("Email de boas-vindas 'enviado' com sucesso!");
    }
}
