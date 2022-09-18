package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.constant.MqConstant;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.to.order.WareDeduceStatusMsg;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.rabbit.RabbitService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;


//监听扣减库存结果
@Slf4j
@Component
public class OrderStockDeduceListener {

    @Autowired
    OrderInfoService orderInfoService;
    @Autowired
    RabbitService rabbitService;

    //如果标注的交换机、队列没有，会自动创建
    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue(name = MqConstant.QUEUE_WARE_ORDER,
                    durable = "true", exclusive = "false", autoDelete = "false"),
                    exchange = @Exchange(name = MqConstant.EXCHANGE_WARE_ORDER,
                            durable = "true", autoDelete = "false", type = "direct"),
                    key = MqConstant.RK_WARE_ORDER)
    })
    public void stockDeduceListener(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //获取消息中的扣减库存结果
        WareDeduceStatusMsg msg = Jsons.toObj(message, WareDeduceStatusMsg.class);
        Long orderId = msg.getOrderId();
        //修改订单状态
        try {
            ProcessStatus status = null;
            switch (msg.getStatus()){
                case "DEDUCTED": status = ProcessStatus.WAITING_DELEVER;break;
                case "OUT_OF_STOCK": status = ProcessStatus.STOCK_OVER_EXCEPTION;break;
                default: status = ProcessStatus.PAID;
            }
            OrderInfo orderInfo = orderInfoService.getById(orderId);
            orderInfoService.changeOrderStatus(orderId,
                    orderInfo.getUserId(),
                    status,
                    Arrays.asList(ProcessStatus.PAID));
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            String uk = SysRedisConst.MQ_RETRY + "stock:order:deduce:" + orderId;
            rabbitService.retryConsumeMsg(3L,uk,deliveryTag,channel);
        }
    }
}
