package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.user.service.UserAddressService;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 14613
* @description 针对表【user_address(用户地址表)】的数据库操作Service实现
* @createDate 2022-09-06 22:20:35
*/
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress>
    implements UserAddressService{

    @Autowired
    UserAddressMapper userAddressMapper;

    /**
     * 获取用户所有的收货地址
     * @return
     */
    @Override
    public List<UserAddress> getUserAddressList() {
        UserAuthInfo authInfo = AuthUtils.getCurrentAuthInfo();
        List<UserAddress> userAddressList = userAddressMapper
                .selectList(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, authInfo.getUserId()));
        return userAddressList;
    }
}




