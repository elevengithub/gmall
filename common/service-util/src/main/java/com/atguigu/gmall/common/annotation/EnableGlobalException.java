package com.atguigu.gmall.common.annotation;

import com.atguigu.gmall.common.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(GlobalExceptionHandler.class)
public @interface EnableGlobalException {
}
