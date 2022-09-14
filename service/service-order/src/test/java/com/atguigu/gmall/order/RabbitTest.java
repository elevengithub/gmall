package com.atguigu.gmall.order;

import com.atguigu.gmall.constant.MqConstant;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitTest {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void test01(){
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_ORDER_EVENT,
                MqConstant.QUEUE_ORDER_DELAY,
                "666");
    }
}
