package com.example.employee.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do RabbitMQ — Exchange, Queue, Binding e JSON converter.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "employee-exchange";
    public static final String QUEUE_NAME = "employee-notifications";
    public static final String ROUTING_KEY = "employee.created";

    @Bean
    public DirectExchange employeeExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue employeeNotificationsQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public Binding employeeBinding(Queue employeeNotificationsQueue, DirectExchange employeeExchange) {
        return BindingBuilder
                .bind(employeeNotificationsQueue)
                .to(employeeExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
