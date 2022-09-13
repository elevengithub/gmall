package com.atguigu.gmall.common.annotation;

import com.atguigu.gmall.common.config.feignclient.FeignClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FeignClientConfiguration.class)
public @interface EnableAutoFeignInterceptor {
}
