package com.atguigu.gmall.item;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BloomerFilterTest {
    
    @Test
    public void testBloomFilter(){
        //1、创建布隆过滤器BloomFilter
        //参数1：传入的数据类型
        //参数2：期望插入的数量
        //参数3：误判率，误判率越高，hash次数越多
        BloomFilter<Long> bloomFilter = BloomFilter.create(
                Funnels.longFunnel(),
                10000,
                0.0001);
        //2、插数据
        for (long i = 0; i < 20; i++) {
            bloomFilter.put(i);
        }
        //3、判断有没有
        System.out.println(bloomFilter.mightContain(4l));
        System.out.println(bloomFilter.mightContain(15l));
        System.out.println(bloomFilter.mightContain(156l));
        System.out.println(bloomFilter.mightContain(143l));
    }
}
