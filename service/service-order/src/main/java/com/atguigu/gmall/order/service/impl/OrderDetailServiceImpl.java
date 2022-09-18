package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.model.order.OrderDetail;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 14613
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2022-09-11 09:18:35
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

    @Override
    public List<OrderDetail> getOrderDetails(Long id, Long userId) {

        return list(new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getUserId,userId)
                .eq(OrderDetail::getOrderId,id));
    }
}




