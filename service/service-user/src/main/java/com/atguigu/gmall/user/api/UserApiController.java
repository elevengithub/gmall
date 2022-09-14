package com.atguigu.gmall.user.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/inner/rpc/user")
@RestController
public class UserApiController {

    @Autowired
    UserAddressService userAddressService;

    /**
     * 获取用户所有的收货地址
     * @return
     */
    @GetMapping("/getUserAddressList")
    public Result<List<UserAddress>> getUserAddressList(){
        List<UserAddress> userAddressList = userAddressService.getUserAddressList();
        return Result.ok(userAddressList);
    }
}
