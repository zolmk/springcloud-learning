package com.fy.springbootrabbitmq.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "spring.rabbitmq.listener.simple.retry", name = "enabled", havingValue = "true")
public class ErrorHandleConfig {
    @Bean
    public Queue errorQueue() {
        return QueueBuilder.durable("error.queue").build();
    }

    @Bean
    public Exchange errorExchange() {
        return ExchangeBuilder.directExchange("error.direct").durable(true).build();
    }

    @Bean
    public Binding binding4(Queue errorQueue, Exchange errorExchange) {
        return BindingBuilder.bind(errorQueue).to(errorExchange).with("error").noargs();
    }

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
    }
}
