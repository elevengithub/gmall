package com.atguigu.gmall.order.api;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.trade.OrderConfirmDataVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inner/rpc/order")
public class OrderApiController {

    @Autowired
    OrderBizService orderBizService;

    /**
     * 获取订单确认页需要的数据
     * @return
     */
    @GetMapping("/confirm/data")
    public Result<OrderConfirmDataVo> getOrderConfirmData(){
        OrderConfirmDataVo orderConfirmDataVo = orderBizService.getOrderConfirmData();
        return Result.ok(orderConfirmDataVo);
    }


    /**
     * 获取某个订单数据
     * @param orderId
     * @return
     */
    @GetMapping("/getOrderInfoById/{orderId}")
    public Result<OrderInfo> getOrderInfo(@PathVariable("orderId") Long orderId){
        Long userId = AuthUtils.getCurrentAuthInfo().getUserId();
        OrderInfo info = orderBizService.getOrderInfoById(orderId, userId);
        return Result.ok(info);
    }
}
