package com.atguigu.starter.cache;

import com.atguigu.starter.cache.aspect.CacheAspect;
import com.atguigu.starter.cache.service.CacheOpsService;
import com.atguigu.starter.cache.service.impl.CacheOpsServiceImpl;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
public class MallCacheAutoConfiguration {

    @Autowired
    RedisProperties redisProperties;

    @Bean
    public CacheAspect cacheAspect(){
        return new CacheAspect();
    }

    @Bean
    public CacheOpsService cacheOpsService(){
        return new CacheOpsServiceImpl();
    }

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
