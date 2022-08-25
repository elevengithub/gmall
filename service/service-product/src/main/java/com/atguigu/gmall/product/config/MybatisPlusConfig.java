package com.atguigu.gmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement //开启声明式事务支持
@Configuration //告诉spring容器这是一个配置类
public class MybatisPlusConfig {

    @Bean //将主体插件注入ioc容器
    public MybatisPlusInterceptor getMybatisPlusInterceptor(){
        //创建主体插件
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //配置分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        //设置溢出处理。即：加入总共有10页，用户查询第100页，则显示最后一页。
        paginationInnerInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }
}
