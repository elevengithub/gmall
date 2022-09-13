package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author 14613
* @description 针对表【order_info(订单表 订单表)】的数据库操作Service实现
* @createDate 2022-09-11 09:18:35
*/
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements OrderInfoService{

}




