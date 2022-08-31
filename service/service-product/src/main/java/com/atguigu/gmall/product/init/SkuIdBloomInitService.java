package com.atguigu.gmall.product.init;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class SkuIdBloomInitService {

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    SkuInfoService skuInfoService;

    @PostConstruct //当前组件对象创建成功之后执行该方法
    public void initBloomSkuId(){
        //1、查出所有的skuId
        List<Long> ids = skuInfoService.getSkuIdList();
        //2、根据redissonClient获取布隆过滤器
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        //3、初始化布隆过滤器
        if (!filter.isExists()) {
            filter.tryInit(5000000,0.0001);
        }
        //4、将所有商品id初始化到布隆过滤器中
        ids.forEach(id -> filter.add(id));
    }
}
