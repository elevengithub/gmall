package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.vo.trade.OrderConfirmDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TradeController {

    @Autowired
    OrderFeignClient orderFeignClient;

    //http://order.gmall.com/trade.html
    @GetMapping("/trade.html")
    public String tradePage(Model model){
        Result<OrderConfirmDataVo> result = orderFeignClient.getOrderConfirmData();
        OrderConfirmDataVo data = result.getData();
        model.addAttribute("detailArrayList",data.getDetailArrayList());
        model.addAttribute("totalNum",data.getTotalNum());
        model.addAttribute("totalAmount",data.getTotalAmount());
        model.addAttribute("userAddressList",data.getUserAddressList());
        model.addAttribute("tradeNo",data.getTradeNo());
        return "order/trade";
    }
}
