package com.atguigu.gmall.pay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.pay.config.AliPayProperties;
import com.atguigu.gmall.pay.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AlipayServiceImpl implements AlipayService {

    @Autowired
    OrderFeignClient orderFeignClient;
    @Autowired
    AlipayClient alipayClient;
    @Autowired
    AliPayProperties properties;

    /**
     * 跳转到支付宝收银台，展示付款二维码
     * @param orderId  订单id
     * @return 支付宝支付后的返回页面
     * @throws AlipayApiException
     */
    @Override
    public String getAlipayPageHtml(Long orderId) throws AlipayApiException {
        //获取订单详请信息
        OrderInfo orderInfo = orderFeignClient.getOrderInfoById(orderId).getData();

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //设置异步回调地址
        request.setNotifyUrl(properties.getNotifyUrl());
        //设置同步回调地址
        request.setReturnUrl(properties.getReturnUrl());
        //封装请求参数
        Map<String,String> params = new HashMap<>();
        params.put("out_trade_no", orderInfo.getOutTradeNo());
        params.put("total_amount", orderInfo.getTotalAmount().toString());
        params.put("subject", "尚品汇订单-"+orderInfo.getOutTradeNo());
        params.put("product_code", "FAST_INSTANT_TRADE_PAY");
        params.put("body",orderInfo.getTradeBody());
        //绝对超时
        String date = DateUtil.formatDate(orderInfo.getExpireTime(), "yyyy-MM-dd HH:mm:ss");
        //自动收单
        params.put("time_expire",date);
        request.setBizContent(Jsons.toStr(params));

        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);

        return response.getBody();
    }

    /**
     * 验证签名
     * @param params 异步通知的参数
     * @return 签名是否正确
     */
    @Override
    public boolean rsaCheckV1(Map<String, String> params) throws AlipayApiException {
        boolean checkV1 = AlipaySignature.rsaCheckV1(params,
                properties.getAlipayPublicKey(),
                properties.getCharset(),
                properties.getSignType());
        return checkV1;
    }
}
