package com.atguigu.gmall.annotation;

import com.atguigu.gmall.rabbit.AppRabbitConfiguration;
import com.atguigu.gmall.rabbit.RabbitService;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({AppRabbitConfiguration.class, RabbitService.class})
public @interface EnableAppRabbit {
}
