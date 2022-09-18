package com.atguigu.gmall.pay;

import com.atguigu.gmall.annotation.EnableAppRabbit;
import com.atguigu.gmall.common.annotation.EnableAutoFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableAutoFeignInterceptor
@EnableAppRabbit
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.order")
@SpringCloudApplication
public class PayMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayMainApplication.class,args);
    }
}