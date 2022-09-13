package com.atguigu.gmall.order;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class OrderTest {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Test
    public void testInsert(){
        OrderInfo orderInfo1 = new OrderInfo();
        orderInfo1.setUserId(1L);
        orderInfo1.setTotalAmount(new BigDecimal("666"));
        orderInfoMapper.insert(orderInfo1);

        OrderInfo orderInfo2 = new OrderInfo();
        orderInfo2.setUserId(2L);
        orderInfo2.setTotalAmount(new BigDecimal("777"));
        orderInfoMapper.insert(orderInfo2);
    }
}
