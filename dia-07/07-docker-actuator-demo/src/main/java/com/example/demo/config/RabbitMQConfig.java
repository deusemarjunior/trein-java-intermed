package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração completa do RabbitMQ — Exchange, Queue, Binding e JSON converter.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "product-exchange";
    public static final String QUEUE_NAME = "product-notifications";
    public static final String ROUTING_KEY = "product.created";

    @Bean
    public DirectExchange productExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue productNotificationsQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public Binding productBinding(Queue productNotificationsQueue, DirectExchange productExchange) {
        return BindingBuilder
                .bind(productNotificationsQueue)
                .to(productExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
