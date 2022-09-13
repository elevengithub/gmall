package com.atguigu.gmall.common.config.feignclient;

import com.atguigu.gmall.common.constant.SysRedisConst;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignClientConfiguration {
    /**
     * 把用户id设置到feign即将发起的新请求中
     * @return
     */
    @Bean
    public RequestInterceptor requestInterceptor(){
//        return new RequestInterceptor() {
//            @Override
//            public void apply(RequestTemplate requestTemplate) {
//
//            }
//        };

        return requestTemplate -> {
            //获取与请求到来tomcat处理请求的线程绑定的ServletRequestAttributes
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            //获取老的请求信息
            HttpServletRequest request = requestAttributes.getRequest();
            //获取老请求中的userId和userTempId
            String userId = request.getHeader(SysRedisConst.USERID_HEADER);
            String userTempId = request.getHeader(SysRedisConst.USERTEMPID_HEADER);
            //设置到feign即将发起的新请求的请求头中
            requestTemplate.header(SysRedisConst.USERID_HEADER,userId);
            requestTemplate.header(SysRedisConst.USERTEMPID_HEADER,userTempId);
        };
    }
}
