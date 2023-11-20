package com.fy.springbootrabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /**
     * 声明一个交换机，如果不存在，会进行创建
     * @return
     */
    @Bean
    public Exchange exchange() {
        return new FanoutExchange("ech1.fanout");
    }

    @Bean(name = "q1")
    public Queue queue() {
        return new Queue("fy");
    }

    /**
     * 声明一个队列，如果不存在，会进行创建
     * @return
     */
    @Bean(name = "q2")
    public Queue queue2() {
        return new Queue("fanout.queue");
    }


    /**
     * 使用 Binding 类将 queue绑定到 exchange 上
     * @return
     */
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue2()).to(exchange()).with("fanout.queue").noargs();
    }


    /**
     * 声明一个Direct Exchange
     */
    @Bean(name = "de")
    public Exchange directExchange() {
        return new DirectExchange("exchange.direct");
    }

    @Bean
    public Binding binding1(Queue q1, Exchange de) {
        return BindingBuilder.bind(q1).to(de).with("cas").noargs();
    }

    @Bean
    public Binding binding2(Queue q2, Exchange de) {
        return BindingBuilder.bind(q2).to(de).with("tt").noargs();
    }

    @Bean
    MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    SimpleConsumeService.CustomRabbitListenerErrorHandler customRabbitListenerErrorHandler() {
        return new SimpleConsumeService.CustomRabbitListenerErrorHandler();
    }

    @Bean
    public DirectExchange exchange23() {
        return ExchangeBuilder.directExchange("delay.direct")
                .durable(true)
                .delayed()
                .build();
    }


}
