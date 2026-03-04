package com.example.employee.messaging;

import com.example.employee.dto.EmployeeCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * TODO 7: Consumer de eventos de funcionários criados.
 *
 * Esta classe deve escutar a fila "employee-notifications" e processar
 * cada EmployeeCreatedEvent recebido, simulando o envio de um email
 * de boas-vindas ao novo funcionário.
 *
 * Passos:
 * 1. Adicione @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME) ao método handleEmployeeCreated()
 * 2. No corpo do método, logue as informações do evento
 * 3. Simule o envio de um email de boas-vindas com log
 *
 * Dica: Veja o OrderNotificationConsumer no projeto demo.
 */
@Component
public class EmployeeNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmployeeNotificationConsumer.class);

    /**
     * TODO 7: Adicione a annotation @RabbitListener e implemente o método.
     *
     * Exemplo:
     *   @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
     *   public void handleEmployeeCreated(EmployeeCreatedEvent event) {
     *       log.info("📥 Evento recebido: Funcionário #{} criado", event.employeeId());
     *       log.info("   Nome: {} ({})", event.name(), event.email());
     *       log.info("   Departamento: {}", event.departmentName());
     *       log.info("   Salário: R$ {}", event.salary());
     *
     *       // Simula envio de email de boas-vindas
     *       log.info("📧 [SIMULADO] Enviando email de boas-vindas para {}", event.email());
     *       log.info("   Assunto: Bem-vindo(a) à empresa, {}!", event.name());
     *       log.info("✅ Email de boas-vindas 'enviado' com sucesso!");
     *   }
     */

    // Descomente e implemente o método acima
}
