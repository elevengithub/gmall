package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 14613
* @description 针对表【user_address(用户地址表)】的数据库操作Service
* @createDate 2022-09-06 22:20:35
*/
public interface UserAddressService extends IService<UserAddress> {

    /**
     * 获取用户所有的收货地址
     * @return
     */
    List<UserAddress> getUserAddressList();
}
