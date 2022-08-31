package com.atguigu.gmall.item.cache.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.cache.CacheOpsService;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheOpsServiceImpl implements CacheOpsService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    @Override
    public <T> T getCacheData(String cacheKey, Class<T> clazz) {
        String str = redisTemplate.opsForValue().get(cacheKey);
        //空值判断
        if (SysRedisConst.NULL_VAL.equals(str)) {
            return null;
        }
        T t = Jsons.toObj(str, clazz);
        return t;
    }

    @Override
    public boolean bloomContains(Long skuId) {
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        return filter.contains(skuId);
    }

    @Override
    public boolean tryLock(Long skuId) {
        //1、准备商品对应的唯一锁
        String lockKey = SysRedisConst.LOCK_SKU_DETAIL + skuId;
        RLock rLock = redissonClient.getLock(lockKey);
        //2、尝试加锁
        boolean isLock = rLock.tryLock();
        return isLock;
    }

    @Override
    public void saveData(String cacheKey, Object obj) {
        if (obj == null) {
            redisTemplate.opsForValue().set(cacheKey,
                    SysRedisConst.NULL_VAL,
                    SysRedisConst.NULL_VAL_CACHETIME,
                    TimeUnit.SECONDS);
        }
        String str = Jsons.toStr(obj);
        redisTemplate.opsForValue().set(cacheKey,str,SysRedisConst.VAL_CACHETIME,TimeUnit.SECONDS);
    }

    @Override
    public void unLock(Long skuId) {
        //1、根据锁唯一标识获取锁
        String lockKey = SysRedisConst.LOCK_SKU_DETAIL + skuId;
        RLock lock = redissonClient.getLock(lockKey);
        //2、释放锁
        lock.unlock();
    }
}
