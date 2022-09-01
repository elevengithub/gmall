package com.atguigu.starter.cache.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {
    String cacheKey() default "";
    String bloomName() default "";//如果指定了布隆过滤器的名字，就用
    String bloomValue() default "";//指定布隆过滤器如果需要判定的话，用什么表达式计算出的值进行判定
    String lockName() default "";//传入精确锁就是用精确锁，未传入就是用全局锁
}
