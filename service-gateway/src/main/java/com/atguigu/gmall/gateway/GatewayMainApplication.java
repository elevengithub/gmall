package com.atguigu.gmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/*
@SpringCloudApplication注解相当于以下三个注解的组合
@SpringBootApplication
@EnableDiscoveryClient   开启服务发现
@EnableCircuitBreaker    开启流量降级、熔断保护支持
 */
@SpringCloudApplication
public class GatewayMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayMainApplication.class,args);
    }
}
