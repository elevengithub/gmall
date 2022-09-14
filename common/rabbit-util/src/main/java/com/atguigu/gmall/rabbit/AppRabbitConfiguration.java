package com.atguigu.gmall.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Slf4j
@EnableRabbit
@Configuration
public class AppRabbitConfiguration {

    @Bean
    public RabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer,
                                         ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        configurer.configure(rabbitTemplate,connectionFactory);
        //设置失败时的重试次数，默认重试3次
        rabbitTemplate.setRetryTemplate(new RetryTemplate());
        //感知消息发送到交换机时的确认函数
        rabbitTemplate.setConfirmCallback((CorrelationData correlationData,
                                           boolean ack,
                                           String cause) -> {
            if (!ack) {
                log.error("消息投递到服务器(交换机)失败，保存到数据库，消息：{}",correlationData);
            }
        });
        //感知消息发送到队列时的返回函数
        rabbitTemplate.setReturnCallback((Message message,
                                          int replyCode,
                                          String replyText,
                                          String exchange,
                                          String routingKey) -> {
            log.error("消息投递到队列失败，保存到数据库，消息：{}",message);
        });
        return rabbitTemplate;
    }
}
