package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.trade.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 14613
* @description 针对表【order_info(订单表 订单表)】的数据库操作Service
* @createDate 2022-09-11 09:18:35
*/
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 修改订单状态
     * @param orderId 订单id
     * @param userId 用户id
     * @param closed 关闭状态
     * @param expected 期望状态
     */
    void changeOrderStatus(Long orderId, Long userId, ProcessStatus closed, List<ProcessStatus> expected);

    /**
     * 保存订单信息到数据库
     * @param tradeNo 订单追踪号
     * @param vo 订单信息
     * @return 订单id
     */
    Long saveOrder(String tradeNo, OrderSubmitVo vo);
}
