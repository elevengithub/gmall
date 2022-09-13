package com.atguigu.gmall.common.auth;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AuthUtils {
    /**
     * 利用Tomcat请求与线程绑定机制。+ Spring自己的 RequestContextHolder ThreadLocal原理
     *      = 同一个请求在处理期间，任何时候都能共享到数据
     * @return
     */
    public static UserAuthInfo getCurrentAuthInfo(){
        UserAuthInfo userAuthInfo = new UserAuthInfo();
        //从RequestContextHolder中获取请求参数
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取请求头中的token
        String userId = attributes.getRequest().getHeader(SysRedisConst.USERID_HEADER);
        //判断存在设置到UserAuthInfo类中
        if(!StringUtils.isEmpty(userId))
        userAuthInfo.setUserId(Long.parseLong(userId));
        //获取请求头中的userTempId，设置到UserAuthInfo类中
        String userTempId = attributes.getRequest().getHeader(SysRedisConst.USERTEMPID_HEADER);
        userAuthInfo.setUserTempId(userTempId);
        return userAuthInfo;
    }
}
