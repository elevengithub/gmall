package com.atguigu.gmall.rabbit;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitService {

    @Autowired
    StringRedisTemplate redisTemplate;

    public void retryConsumeMsg(Long num, String uk, Long tag, Channel channel) throws IOException {
        Long increment = redisTemplate.opsForValue()
                .increment(uk);
        if (increment <= num) {
            channel.basicNack(tag,false,true);
        } else {
            channel.basicNack(tag,false,false);
            redisTemplate.delete(uk);
        }
    }
}
