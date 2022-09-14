package com.atguigu.gmall.order;

import com.atguigu.gmall.annotation.EnableAppRabbit;
import com.atguigu.gmall.common.annotation.EnableAutoFeignInterceptor;
import com.atguigu.gmall.common.annotation.EnableGlobalException;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAppRabbit
@EnableTransactionManagement
@EnableGlobalException
@EnableAutoFeignInterceptor
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.cart",
                                    "com.atguigu.gmall.feign.user",
                                    "com.atguigu.gmall.feign.product",
                                    "com.atguigu.gmall.feign.ware"})
@MapperScan(basePackages = "com.atguigu.gmall.order.mapper")
@SpringCloudApplication
public class OrderMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderMainApplication.class,args);
    }
}
