package com.atguigu.gmall.product.bloom.impl;

import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BloomOpsServiceImpl implements BloomOpsService {

    @Autowired
    RedissonClient client;

    @Override
    public void rebuildBloom(String bloomName, BloomDataQueryService bloomDataQueryService) {
        //1、先准备一个新的布隆过滤器
        RBloomFilter<Object> filter = client.getBloomFilter(bloomName + "_NEW");
        //2、初始化布隆过滤器
        filter.tryInit(5000000,0.00001);
        //3、获取需要初始化到布隆过滤器的数据
        List list = bloomDataQueryService.getBloomData();
        //4、将数据初始化到布隆过滤器中
        list.forEach(data -> filter.add(data));
        //5、新布隆过滤器替换掉旧布隆过滤器
        //获取到旧布隆过滤器下线
        RBloomFilter<Object> oldBloomFilter = client.getBloomFilter(bloomName);
        oldBloomFilter.rename("temp");
        //新布隆过滤器上线
        filter.rename(bloomName);
        //6、删除旧的布隆过滤器和中间布隆过滤器
        oldBloomFilter.deleteAsync();
        client.getBloomFilter("temp").deleteAsync();
    }
}
