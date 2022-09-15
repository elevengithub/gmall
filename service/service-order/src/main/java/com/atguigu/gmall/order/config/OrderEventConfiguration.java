package com.atguigu.gmall.order.config;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.constant.MqConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class OrderEventConfiguration {

    /**
     * String name,  交换机名字
     * boolean durable,  是否持久化
     * boolean autoDelete,  是否自动删除
     * Map<String, Object> arguments  其他参数设置
     * @return
     */
    @Bean
    public Exchange orderEventExchange(){
        Exchange exchange = new TopicExchange(MqConstant.EXCHANGE_ORDER_EVENT,
                true,false);
        return exchange;
    }

    /**
     * String name,
     * boolean durable,
     * boolean exclusive, 是否排他
     * boolean autoDelete,
     * Map<String, Object> arguments
     * @return
     */
    @Bean
    public Queue orderDelayQueue(){
        Map<String, Object> arguments = new HashMap<>();
        //设置队列中消息的过期时间
        arguments.put("x-message-ttl", SysRedisConst.ORDER_CLOSE_TTL * 1000);
        //设置消息过期后发送到哪个交换机
        arguments.put("x-dead-letter-exchange",MqConstant.EXCHANGE_ORDER_EVENT);
        //设置发送到交换机的路由键
        arguments.put("x-dead-letter-routing-key",MqConstant.RK_ORDER_DEAD);

        return new Queue(MqConstant.QUEUE_ORDER_DELAY,
                true, false,false,arguments);
    }

    /**
     * String destination,  目的地
     * Binding.DestinationType destinationType,  目的地类型
     * String exchange,  交换机
     * String routingKey,  路由键
     * Map<String, Object> arguments  其他参数
     * @return
     */
    @Bean
    public Binding orderDelayQueueBinding(){
        return new Binding(MqConstant.QUEUE_ORDER_DELAY,
                Binding.DestinationType.QUEUE,
                MqConstant.EXCHANGE_ORDER_EVENT,
                MqConstant.RK_ORDER_CREATED,
                null);
    }

    @Bean
    public Queue orderDeadQueue(){
        return new Queue(MqConstant.QUEUE_ORDER_DEAD,true,false,false);
    }

    @Bean
    public Binding orderDeadQueueBinding(){
        return new Binding(MqConstant.QUEUE_ORDER_DEAD,
                Binding.DestinationType.QUEUE,
                MqConstant.EXCHANGE_ORDER_EVENT,
                MqConstant.RK_ORDER_DEAD,
                null);
    }
}
