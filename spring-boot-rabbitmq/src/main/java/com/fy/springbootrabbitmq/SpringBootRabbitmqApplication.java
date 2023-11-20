package com.fy.springbootrabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.postprocessor.AbstractCompressingPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.io.OutputStream;

@SpringBootApplication
@Slf4j
public class SpringBootRabbitmqApplication implements ApplicationContextAware {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRabbitmqApplication.class, args);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("message: {} replyCode: {} replyText: {} exchange: {} routingKey: {}", message.toString(), replyCode, replyText, exchange, routingKey);
            }
        });
    }
}
