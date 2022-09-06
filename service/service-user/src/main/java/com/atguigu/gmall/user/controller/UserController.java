package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessVo;
import com.atguigu.gmall.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/user")
@RestController
public class UserController {

    @Autowired
    UserInfoService userInfoService;

    /**
     * 用户登录  http://api.gmall.com/api/user/passport/login
     * @param userInfo
     * @return
     */
    @PostMapping("/passport/login")
    public Result login(@RequestBody UserInfo userInfo){
        LoginSuccessVo successVo = userInfoService.login(userInfo);
        if (successVo != null) {
            return Result.ok(successVo);
        }
        return Result.build("", ResultCodeEnum.LOGIN_FAILE);
    }

    /**
     * 退出登录 http://api.gmall.com/api/user/passport/logout
     * @return
     */
    @GetMapping("/passport/logout")
    public Result logout(@RequestHeader("token") String token){
        userInfoService.logout(token);
        return Result.ok();
    }
}
