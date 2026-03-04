package com.example.demo.health;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom HealthIndicator para verificar conectividade com RabbitMQ.
 *
 * Aparece no endpoint /actuator/health como "rabbitMQCustom".
 */
@Component("rabbitMQCustom")
public class RabbitMQHealthIndicator implements HealthIndicator {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQHealthIndicator(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Health health() {
        try {
            // Tenta obter a conexão com o RabbitMQ
            rabbitTemplate.execute(channel -> {
                channel.basicQos(1);
                return null;
            });

            return Health.up()
                    .withDetail("service", "RabbitMQ")
                    .withDetail("status", "Conectado")
                    .build();
        } catch (Exception ex) {
            return Health.down()
                    .withDetail("service", "RabbitMQ")
                    .withDetail("error", ex.getMessage())
                    .build();
        }
    }
}
