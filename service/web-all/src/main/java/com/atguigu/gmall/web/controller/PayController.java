package com.atguigu.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PayController {

    //http://payment.gmall.com/pay.html?orderId=776871273182527488
    @GetMapping("/pay.html")
    public String payPage(@RequestParam("orderId") Long orderId){
        return "payment/pay";
    }
}
