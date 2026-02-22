package com.example.employee.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO 6: Configuração do RabbitMQ.
 *
 * Esta classe já define as constantes e o converter JSON.
 * Você precisa criar os beans para:
 * 1. DirectExchange
 * 2. Queue
 * 3. Binding (conecta a Queue ao Exchange via routing key)
 *
 * Dica: Veja a classe RabbitMQConfig no projeto demo (06-persistence-messaging-demo).
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "employee-exchange";
    public static final String QUEUE_NAME = "employee-notifications";
    public static final String ROUTING_KEY = "employee.created";

    // TODO 6a: Crie o bean DirectExchange
    //
    // @Bean
    // public DirectExchange employeeExchange() {
    //     return new DirectExchange(EXCHANGE_NAME);
    // }

    // TODO 6b: Crie o bean Queue
    //
    // @Bean
    // public Queue employeeNotificationsQueue() {
    //     return QueueBuilder.durable(QUEUE_NAME).build();
    // }

    // TODO 6c: Crie o bean Binding
    //
    // @Bean
    // public Binding employeeBinding(Queue employeeNotificationsQueue, DirectExchange employeeExchange) {
    //     return BindingBuilder
    //             .bind(employeeNotificationsQueue)
    //             .to(employeeExchange)
    //             .with(ROUTING_KEY);
    // }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
