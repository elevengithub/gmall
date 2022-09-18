package com.atguigu.gmall.order.biz;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.trade.OrderConfirmDataVo;
import com.atguigu.gmall.model.vo.trade.OrderSubmitVo;
import com.atguigu.gmall.model.vo.trade.OrderWareMapVo;
import com.atguigu.gmall.model.vo.trade.WareChildOrderVo;

import java.util.List;

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

    /**
     * 根据订单id获取订单信息
     * @param orderId 订单id
     * @return 订单详情
     */
    OrderInfo getOrderInfoById(Long orderId,Long userId);

    /**
     * 拆单
     * @param params
     * @return
     */
    List<WareChildOrderVo> orderSplit(OrderWareMapVo params);
}
