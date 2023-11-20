package com.fy.springbootrabbitmq;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fy.springbootrabbitmq.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class SimpleConsumeService {

    @RabbitListener(queues = "fanout.queue")
    public void fq(String s) {
        System.err.println("Receive: " + s);
    }

    /**
     * 通过注解方式来声明和绑定 queue和 exchange
     */

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("topic.queue.1"),
            exchange = @Exchange(name = "ech2.topic", type = ExchangeTypes.TOPIC),
            key = {"ch.*"}
    ))
    public void handlerOne(String s) {
        System.out.println("ch.* : "+s);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("topic.queue.2"),
            exchange = @Exchange(name = "ech2.topic", type = ExchangeTypes.TOPIC),
            key = {"*.news"}
    ))
    public void handlerTwo(String s) {
        System.out.println("*.news : "+s);
    }

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "fy"),
            exchange = @Exchange(name = "exchange.direct", type = ExchangeTypes.DIRECT),
            key = {"cas"}
    ), errorHandler = "customRabbitListenerErrorHandler")
    public void handlerObj(UserDTO userDTO) throws JsonProcessingException {
        System.out.println("收到对象："+ this.objectMapper.writeValueAsString(userDTO));
    }

    /**
     * 声明惰性队列
     */
    @RabbitListener(
            queuesToDeclare = @Queue(
                    name = "queue.lazy",
                    arguments = @Argument(name = "x-queue-mode", value = "lazy")
            )
    )
    public void handlerLazyQueue(UserDTO userDTO) {
        // do something
        System.out.println("HHHHHHHHHHHHHHHHHHHHHH");
        throw new RuntimeException("故意");
    }


    /**
     * 声明延时交换机
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "delay.queue", durable = "true"),
            exchange = @Exchange(name = "delay.direct", type = "direct", durable = "true", delayed = "true"),
            key = {"hi"}
    ))
    public void listenDelayMessage(String msg) {
        log.info("接收到延时消息：{}", msg);
    }





    public static class CustomRabbitListenerErrorHandler implements RabbitListenerErrorHandler {

        @Override
        public Object handleError(Message amqpMessage, org.springframework.messaging.Message<?> message, ListenerExecutionFailedException exception) throws Exception {
            if (exception.getCause() instanceof MessageConversionException) {
                return new String(amqpMessage.getBody(), StandardCharsets.UTF_8);
            } else {
                System.err.println("执行出错");
            }
            return null;
        }
    }
}
