package com.example.employee.health;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * TODO 5: Implemente um Custom HealthIndicator para RabbitMQ.
 *
 * Este componente aparecerá no endpoint /actuator/health como "rabbitMQCustom".
 *
 * Passos:
 * 1. Implemente a interface HealthIndicator
 * 2. No método health(), tente executar um comando no RabbitMQ
 * 3. Se conectar com sucesso, retorne Health.up() com detalhes
 * 4. Se falhar, retorne Health.down() com a mensagem de erro
 *
 * Exemplo de implementação:
 *
 *   @Override
 *   public Health health() {
 *       try {
 *           rabbitTemplate.execute(channel -> {
 *               channel.basicQos(1);
 *               return null;
 *           });
 *           return Health.up()
 *                   .withDetail("service", "RabbitMQ")
 *                   .withDetail("status", "Conectado")
 *                   .build();
 *       } catch (Exception ex) {
 *           return Health.down()
 *                   .withDetail("service", "RabbitMQ")
 *                   .withDetail("error", ex.getMessage())
 *                   .build();
 *       }
 *   }
 */
@Component("rabbitMQCustom")
public class RabbitMQHealthIndicator implements HealthIndicator {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQHealthIndicator(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Health health() {
        // TODO 5: Implemente a verificação de saúde do RabbitMQ
        //
        // try {
        //     rabbitTemplate.execute(channel -> {
        //         channel.basicQos(1);
        //         return null;
        //     });
        //     return Health.up()
        //             .withDetail("service", "RabbitMQ")
        //             .withDetail("status", "Conectado")
        //             .build();
        // } catch (Exception ex) {
        //     return Health.down()
        //             .withDetail("service", "RabbitMQ")
        //             .withDetail("error", ex.getMessage())
        //             .build();
        // }

        // Placeholder — retorna UNKNOWN até implementar
        return Health.unknown()
                .withDetail("service", "RabbitMQ")
                .withDetail("status", "TODO 5 — não implementado")
                .build();
    }
}
