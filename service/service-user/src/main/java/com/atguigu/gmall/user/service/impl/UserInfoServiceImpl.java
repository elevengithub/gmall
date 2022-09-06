package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessVo;
import com.atguigu.starter.cache.constant.SysRedisConst;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* @author 14613
* @description 针对表【user_info(用户表)】的数据库操作Service实现
* @createDate 2022-09-06 22:20:35
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

    @Resource
    UserInfoMapper userInfoMapper;
    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 用户登录
     * @param userInfo
     * @return
     */
    @Override
    public LoginSuccessVo login(UserInfo userInfo) {
        //根据用户名和密码查询用户
        UserInfo user = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getName, userInfo.getLoginName())
                .eq(UserInfo::getPasswd, MD5.encrypt(userInfo.getPasswd())));
        if (user != null) {
            //生成自定义加密token
            String token = UUID.randomUUID().toString().replace("-", "");
            //存入redis缓存
            redisTemplate.opsForValue().set(SysRedisConst.USER_LOGIN + token,
                    Jsons.toStr(userInfo),7, TimeUnit.DAYS);
            LoginSuccessVo successVo = new LoginSuccessVo();
            successVo.setToken(token);
            successVo.setNickName(user.getNickName());
            return successVo;
        }
        return null;
    }

    /**
     * 退出登录
     * @param token  用户登录令牌
     */
    @Override
    public void logout(String token) {
        redisTemplate.delete(token);
    }
}




