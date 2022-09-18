package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.constant.MqConstant;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.model.to.order.WareDeduceMsg;
import com.atguigu.gmall.model.to.order.WareDeduceSkuInfo;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.atguigu.gmall.rabbit.RabbitService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderPayedListener {

    @Autowired
    RabbitService rabbitService;
    @Autowired
    PaymentInfoService paymentInfoService;
    @Autowired
    OrderInfoService orderInfoService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    OrderBizService orderBizService;
    @Autowired
    OrderDetailService orderDetailService;

    @RabbitListener(queues = {MqConstant.QUEUE_ORDER_PAYED})
    public void changeOrderStatus(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //1、拿到消息
        Map<String, String> map = Jsons.toObj(message, Map.class);
        String tradeNo = map.get("trade_no");
        try {
            //2、保存订单支付信息
            PaymentInfo paymentInfo = paymentInfoService.savePaymentInfo(map);
            //3、修改订单状态
            orderInfoService.changeOrderStatus(paymentInfo.getOrderId(),
                    paymentInfo.getUserId(),
                    ProcessStatus.PAID,
                    Arrays.asList(ProcessStatus.UNPAID, ProcessStatus.CLOSED));
            //4、通知库存服务减库存
            //4.1、准备消息内容
            WareDeduceMsg msg = prepareWareDeduceMsg(paymentInfo);
            //4.2、发送消息
            rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_WARE_EVENT,
                    MqConstant.RK_WARE_DEDUCE,
                    Jsons.toStr(msg));
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            String uk = SysRedisConst.MQ_RETRY + "order:payed:" + tradeNo;
            rabbitService.retryConsumeMsg(3L, uk, deliveryTag, channel);
        }
    }

    private WareDeduceMsg prepareWareDeduceMsg(PaymentInfo paymentInfo) {
        WareDeduceMsg msg = new WareDeduceMsg();
        msg.setOrderId(paymentInfo.getOrderId());
        //查询当前订单
        OrderInfo orderInfo = orderBizService.getOrderInfoById(paymentInfo.getOrderId(),
                paymentInfo.getUserId());
        msg.setConsignee(orderInfo.getConsignee());
        msg.setConsigneeTel(orderInfo.getConsigneeTel());
        msg.setConsignee(orderInfo.getConsignee());
        msg.setConsigneeTel(orderInfo.getConsigneeTel());
        msg.setOrderComment(orderInfo.getOrderComment());
        msg.setOrderBody(orderInfo.getTradeBody());
        msg.setDeliveryAddress(orderInfo.getDeliveryAddress());
        msg.setPaymentWay("2");
        //查询订单详情
        List<WareDeduceSkuInfo> wareDeduceSkuInfos = orderDetailService.list(new LambdaQueryWrapper<OrderDetail>()
                        .eq(OrderDetail::getUserId, paymentInfo.getUserId())
                        .eq(OrderDetail::getOrderId, paymentInfo.getOrderId()))
                .stream().map(orderDetail -> {
                    WareDeduceSkuInfo wareDeduceSkuInfo = new WareDeduceSkuInfo();
                    wareDeduceSkuInfo.setSkuId(orderDetail.getSkuId());
                    wareDeduceSkuInfo.setSkuNum(orderDetail.getSkuNum());
                    wareDeduceSkuInfo.setSkuName(orderDetail.getSkuName());
                    return wareDeduceSkuInfo;
                }).collect(Collectors.toList());
        msg.setDetails(wareDeduceSkuInfos);
        return msg;
    }
}
