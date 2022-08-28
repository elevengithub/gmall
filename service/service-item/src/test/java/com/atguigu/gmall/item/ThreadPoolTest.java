package com.atguigu.gmall.item;

import com.atguigu.gmall.common.config.threadpool.AppThreadPoolAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ThreadPoolExecutor;

@SpringBootTest
public class ThreadPoolTest {

    @Autowired
    AppThreadPoolAutoConfiguration configuration;

    @Test
    public void testThreadPool(){
        ThreadPoolExecutor executor = configuration.getThreadPoolExecutor();
        executor.submit(() -> {
            System.out.println(Thread.currentThread().getName() + ": hehe");
        });
    }
}
