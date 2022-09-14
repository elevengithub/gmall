package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.to.order.OrderMsg;
import com.atguigu.gmall.order.biz.OrderBizService;
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
    StringRedisTemplate redisTemplate;

    @RabbitListener
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
            //重试10次再次关闭订单
            Long increment = redisTemplate.opsForValue()
                    .increment(SysRedisConst.MQ_RETRY + "order:" + orderMsg.getOrderId());
            if (increment <= 10) {
                //尝试次数未达到十次，重新放回延迟队列
                channel.basicNack(deliveryTag,false,true);
            } else {
                //尝试超过十次，不重新入队
                channel.basicNack(deliveryTag,false,false);
                //删除redis中记录重试次数的自增key
                redisTemplate.delete(SysRedisConst.MQ_RETRY + "order:" + orderMsg.getOrderId());
            }
        }
    }
}
