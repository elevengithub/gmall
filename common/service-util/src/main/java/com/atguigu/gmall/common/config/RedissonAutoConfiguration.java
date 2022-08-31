package com.atguigu.gmall.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//需要使用Redis的连接配置，等到Redis自动配置完成之后再进行自动配置
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
public class RedissonAutoConfiguration {

    @Autowired
    RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient(){
        //1、准备RedissonClient连接所需要的配置
        Config config = new Config();
        String host = redisProperties.getHost();
        int port = redisProperties.getPort();
        String password = redisProperties.getPassword();
        //2、制定好Redisson的配置项
        //Redis url should start with redis:// or rediss:// (for SSL connection)
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password);
        //3、创建一个RedissonClient
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
