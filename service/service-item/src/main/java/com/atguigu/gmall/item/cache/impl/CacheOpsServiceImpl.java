package com.atguigu.gmall.item.cache.impl;

import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.cache.CacheOpsService;

import com.atguigu.starter.cache.constant.SysRedisConst;
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

    /**
     * 查询缓存
     * @param cacheKey  缓存时的key值
     * @param clazz   缓存的value值类型
     * @return    返回缓存对象
     */
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

    /**
     * 布隆过滤器判定数据是否存在
     * @param skuId  要判断的id
     * @return  返回是否存在
     */
    @Override
    public boolean bloomContains(Long skuId) {
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        return filter.contains(skuId);
    }

    /**
     * 加锁
     * @param skuId  需要加锁的id
     * @return
     */
    @Override
    public boolean tryLock(Long skuId) {
        //1、准备商品对应的唯一锁
        String lockKey = SysRedisConst.LOCK_SKU_DETAIL + skuId;
        RLock rLock = redissonClient.getLock(lockKey);
        //2、尝试加锁
        boolean isLock = rLock.tryLock();
        return isLock;
    }

    /**
     * 将数据放入缓存
     * @param cacheKey   缓存key值
     * @param obj     缓存的对象
     */
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

    /**
     * 根据商品id释放锁
     * @param skuId  商品id
     */
    @Override
    public void unLock(Long skuId) {
        //1、根据锁唯一标识获取锁
        String lockKey = SysRedisConst.LOCK_SKU_DETAIL + skuId;
        RLock lock = redissonClient.getLock(lockKey);
        //2、释放锁
        lock.unlock();
    }
}
