package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 14613
* @description 针对表【order_detail(订单明细表)】的数据库操作Service
* @createDate 2022-09-11 09:18:35
*/
public interface OrderDetailService extends IService<OrderDetail> {

    List<OrderDetail> getOrderDetails(Long id, Long userId);
}
