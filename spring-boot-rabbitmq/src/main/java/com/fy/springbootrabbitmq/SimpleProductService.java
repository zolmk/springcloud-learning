package com.fy.springbootrabbitmq;


import com.fy.springbootrabbitmq.dto.UserDTO;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;


/*
    使用Test类来实现生产者
 */
@Deprecated
@Component
public class SimpleProductService implements ApplicationRunner {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Override
    public void run(ApplicationArguments args) throws Exception {


    }

}
