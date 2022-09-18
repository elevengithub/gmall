package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.constant.MqConstant;
import com.atguigu.gmall.model.to.order.OrderMsg;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.rabbit.RabbitService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@Component
public class OrderCloseListener {

    @Autowired
    OrderBizService orderBizService;
    @Autowired
    RabbitService rabbitService;

    /**
     * 监听关闭订单队列，消费消息，进行关单操作
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = MqConstant.QUEUE_ORDER_DEAD)
    public void orderClose(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //1、拿到订单消息
        OrderMsg orderMsg = Jsons.toObj(message, OrderMsg.class);
        try {
            //2、关闭订单
            log.info("监听到超时订单{},正在关闭：",orderMsg);
            orderBizService.closeOrder(orderMsg.getOrderId(),orderMsg.getUserId());
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            log.error("订单关闭业务失败。消息：{}，失败原因：{}",orderMsg,e);
            //重试3次再次关闭订单
            String uk = SysRedisConst.MQ_RETRY + "order:" + orderMsg.getOrderId();
            rabbitService.retryConsumeMsg(3L,uk,deliveryTag,channel);
        }
    }
}
