package com.atguigu.gmall.pay.service;


import com.alipay.api.AlipayApiException;

import java.util.Map;

public interface AlipayService {

    /**
     * 跳转到支付宝收银台，展示付款二维码
     * @param orderId  订单id
     * @return 支付宝支付后的返回页面
     * @throws AlipayApiException
     */
    String getAlipayPageHtml(Long orderId) throws AlipayApiException;

    /**
     * 验证签名
     * @param params 异步通知的参数
     * @return 签名是否正确
     */
    boolean rsaCheckV1(Map<String, String> params) throws AlipayApiException;
}
