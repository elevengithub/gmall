package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.starter.cache.util.Jsons;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.atguigu.gmall.order.mapper.PaymentInfoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author 14613
 * @description 针对表【payment_info(支付信息表)】的数据库操作Service实现
 * @createDate 2022-09-11 09:18:35
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
        implements PaymentInfoService {

    @Resource
    OrderInfoService orderInfoService;

    /**
     * 保存支付信息到数据库
     *
     * @param map 支付成功后支付宝异步返回的数据
     * @return 支付信息
     */
    @Override
    public PaymentInfo savePaymentInfo(Map<String, String> map) {
        //先判断数据库中是否已经存在此支付订单号对应的订单支付信息
        //防止消息重复消费，同一份订单支付数据多次存入数据库，保证幂等性操作
        String outTradeNo = map.get("out_trade_no");
        Long userId = Long.parseLong(outTradeNo.split("_")[1]);
        PaymentInfo one = this.getOne(new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getUserId, userId)
                .eq(PaymentInfo::getOutTradeNo, outTradeNo));
        if (one != null) {
            return one;
        }
        //走到这里表示数据库中不存在该支付订单信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(outTradeNo);
        //根据用户id和对外交易号查询订单id
        OrderInfo orderInfo = orderInfoService.getOne(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getUserId, userId)
                .eq(OrderInfo::getOutTradeNo, outTradeNo));
        paymentInfo.setUserId(orderInfo.getUserId());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType("2");
        paymentInfo.setTradeNo(map.get("trade_no"));
        paymentInfo.setTotalAmount(new BigDecimal(map.get("total_amount") + ""));
        paymentInfo.setSubject(map.get("subject"));
        paymentInfo.setPaymentStatus(map.get("trade_status"));
        paymentInfo.setCreateTime(new Date());
        Date callbackTime = DateUtil.parseDate(map.get("notify_time"), "yyyy-MM-dd HH:mm:ss");
        paymentInfo.setCallbackTime(callbackTime);

        //回调内容
        paymentInfo.setCallbackContent(Jsons.toStr(map));

        save(paymentInfo);
        return paymentInfo;
    }
}




