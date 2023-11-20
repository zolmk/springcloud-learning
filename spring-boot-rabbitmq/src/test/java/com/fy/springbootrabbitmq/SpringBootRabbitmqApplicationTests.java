package com.fy.springbootrabbitmq;

import com.fy.springbootrabbitmq.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.charset.StandardCharsets;

@SpringBootTest
@Slf4j
class SpringBootRabbitmqApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    void sendSimpleMessage() {
        // 直接发送到队列
        rabbitTemplate.convertAndSend("fy", "Hello world.");
        // 发送到交换机，第二个参数可空
        rabbitTemplate.convertAndSend("ech1.fanout", "", "Hello world.");

        // 发送到直接交换机
        rabbitTemplate.convertAndSend("exchange.direct", "cas", "HHHHH");
    }

    @Test
    void sendObjMessage() {
        // 发送对象到交换机
        UserDTO userDTO = new UserDTO("zolmk", 123);
        rabbitTemplate.convertAndSend("exchange.direct", "cas", userDTO);
        // 使用消息确认回调机制
        // 用它来关联消息
        CorrelationData data = new CorrelationData();
        data.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("发生错误", ex);
            }

            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                if (result.isAck()) {
                    log.info("消息发送成功！");
                } else {
                    log.error("MQ接收消息失败，原因【{}】", result.getReason());
                }
            }
        });
        Message message = MessageBuilder.withBody("".getBytes(StandardCharsets.UTF_8)).build();
        rabbitTemplate.convertAndSend("exchange.direct", "cas", userDTO, data);
    }


    @Test
    void sendDelayMsg() {
        String message = "hello, delay message";
        rabbitTemplate.convertAndSend("delay.direct", "hi", message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                // 设置延时10秒
                message.getMessageProperties().setDelay(10000);
                return message;
            }
        });
    }


    @Test
    void contextLoads() {
    }

}
