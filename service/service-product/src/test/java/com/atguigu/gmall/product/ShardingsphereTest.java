package com.atguigu.gmall.product;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ShardingsphereTest {

    @Resource
    BaseTrademarkMapper baseTrademarkMapper;

    @Test
    public void test01(){
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(4);
        System.out.println("baseTrademark = " + baseTrademark);

        BaseTrademark baseTrademark1 = baseTrademarkMapper.selectById(4);
        System.out.println("baseTrademark1 = " + baseTrademark1);

        BaseTrademark baseTrademark2 = baseTrademarkMapper.selectById(4);
        System.out.println("baseTrademark2 = " + baseTrademark2);

        BaseTrademark baseTrademark3 = baseTrademarkMapper.selectById(4);
        System.out.println("baseTrademark3 = " + baseTrademark3);
    }
}
