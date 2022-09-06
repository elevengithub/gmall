package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 14613
* @description 针对表【user_info(用户表)】的数据库操作Service
* @createDate 2022-09-06 22:20:35
*/
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 用户登录
     * @param userInfo  用户登录信息
     * @return
     */
    LoginSuccessVo login(UserInfo userInfo);

    /**
     * 退出登录
     * @param token  用户登录令牌
     */
    void logout(String token);
}
