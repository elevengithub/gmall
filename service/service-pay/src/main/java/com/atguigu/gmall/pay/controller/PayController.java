package com.atguigu.gmall.pay.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.pay.service.AlipayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequestMapping("/api/payment")
@Controller
public class PayController {

    @Autowired
    AlipayService alipayService;

    /**
     * 买家账号 tawmvu1129@sandbox.com
     * 登录密码111111
     * 支付密码111111
     *
     * 电脑网站支付文档：https://opendocs.alipay.com/open/270/106291?ref=api
     * 跳到 支付宝的二维码收银台
     * @return
     */
    @ResponseBody
    @GetMapping("/alipay/submit/{orderId}")
    public String alipayPage(@PathVariable("orderId") Long orderId) throws AlipayApiException {
        String content = alipayService.getAlipayPageHtml(orderId);
        return content;
    }

    /**
     * 支付成功：同步跳转
     * 跳到支付成功页；
     * 支付成功以后，支付宝会命令浏览器 来到 http://gmall.com/api/payment/paysuccess
     * @return
     */
    @GetMapping("/paysuccess") //同步通知地址
    public String paySuccess(@RequestParam Map<String,String> paramMaps) throws AlipayApiException {

        return "redirect:http://gmall.com/pay/success.html";
    }

    /**
     * 支付成功：异步通知
     * @param params  alipay返回的数据
     * @return
     */
    @ResponseBody
    @RequestMapping("/success/notify")
    public String notifySuccess(@RequestParam Map<String,String> params) throws AlipayApiException {
        boolean result = alipayService.rsaCheckV1(params);
        if (result) {
            log.info("支付成功，异步通知抵达，验签通过。{}", Jsons.toStr(params));
            //TODO 修改订单状态

            return "success";
        } else {
            return "fail";
        }
    }
}
