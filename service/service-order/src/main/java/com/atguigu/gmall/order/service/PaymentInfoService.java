package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.payment.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author 14613
* @description 针对表【payment_info(支付信息表)】的数据库操作Service
* @createDate 2022-09-11 09:18:35
*/
public interface PaymentInfoService extends IService<PaymentInfo> {

    /**
     * 保存支付信息到数据库
     * @param map 支付成功后支付宝异步返回的数据
     * @return  支付信息
     */
    PaymentInfo savePaymentInfo(Map<String,String> map);
}
