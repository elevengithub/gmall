package com.atguigu.gmall.common.config.threadpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
@EnableConfigurationProperties(AppThreadPoolProperties.class)
public class AppThreadPoolAutoConfiguration {

    @Autowired
    AppThreadPoolProperties properties;

    @Value("spring.application.name")
    private String springApplicationName;

    @Bean
    public ThreadPoolExecutor getThreadPoolExecutor(){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaximumPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(properties.getQueueSize()),
                new ThreadFactory() {
                    int i = 1;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName(springApplicationName + "[core-thread-" + i++ + "]");
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
