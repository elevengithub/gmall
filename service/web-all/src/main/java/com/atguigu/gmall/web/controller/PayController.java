package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class PayController {

    @Autowired
    OrderFeignClient orderFeignClient;

    //http://payment.gmall.com/pay.html?orderId=776871273182527488
    @GetMapping("/pay.html")
    public String payPage(@RequestParam("orderId") Long orderId, Model model){
        Result<OrderInfo> result = orderFeignClient.getOrderInfoById(orderId);
        OrderInfo orderInfo = result.getData();
        //判断订单是否过期，如果订单未过期，显示订单
        Date ttl = orderInfo.getExpireTime();
        if (ttl.after(new Date())) {
            model.addAttribute("orderInfo",orderInfo);
            return "payment/pay";
        }
        return "payment/error";
    }

    @GetMapping("/pay/success.html")
    public String paySuccess(){
        return "payment/success";
    }
}
