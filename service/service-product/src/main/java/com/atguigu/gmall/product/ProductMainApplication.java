package com.atguigu.gmall.product;

import com.atguigu.gmall.common.config.Swagger2Config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.search")
@EnableScheduling
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper")
@Import({Swagger2Config.class}) //导入指定类
@SpringCloudApplication
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class,args);
    }
}
