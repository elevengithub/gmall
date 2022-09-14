package com.atguigu.gmall.order.biz;

import com.atguigu.gmall.model.vo.trade.OrderConfirmDataVo;
import com.atguigu.gmall.model.vo.trade.OrderSubmitVo;

public interface OrderBizService {

    /**
     * 获取订单确认页需要的数据
     * @return
     */
    OrderConfirmDataVo getOrderConfirmData();

    /**
     * 保存订单信息到数据库
     * @param tradeNo 订单唯一追踪号
     * @param vo 订单信息vo类
     * @return 订单id
     */
    Long submitOrder(String tradeNo, OrderSubmitVo vo);

    /**
     * 关闭订单
     * @param orderId  订单id
     * @param userId  用户id
     */
    void closeOrder(Long orderId, Long userId);
}
