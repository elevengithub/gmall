package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.trade.OrderSubmitVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/order/auth")
@RestController
public class OrderRestController {

    @Autowired
    OrderBizService orderBizService;

    /**
     * 保存订单信息到数据库
     * @param tradeNo 订单唯一追踪号
     * @param vo 订单信息vo类
     * @return 订单id
     */
    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo") String tradeNo,
                              @RequestBody OrderSubmitVo vo){
        Long orderId = orderBizService.submitOrder(tradeNo,vo);
        return Result.ok(orderId.toString());
    }
}
