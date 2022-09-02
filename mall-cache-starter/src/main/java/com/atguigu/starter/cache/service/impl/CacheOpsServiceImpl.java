package com.atguigu.starter.cache.service.impl;

import com.atguigu.starter.cache.constant.SysRedisConst;
import com.atguigu.starter.cache.service.CacheOpsService;
import com.atguigu.starter.cache.util.Jsons;
import com.fasterxml.jackson.core.type.TypeReference;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.lang.reflect.Type;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class CacheOpsServiceImpl implements CacheOpsService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    //执行延迟任务线程池
    ScheduledThreadPoolExecutor scheduledThreadPool = new ScheduledThreadPoolExecutor(4);

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

    /**
     * 查询缓存
     * @param cacheKey  查询缓存使用的cacheKey
     * @param returnType  查询缓存结束后返回结果的类型
     * @return
     */
    @Override
    public Object getCacheData(String cacheKey, Type returnType) {
        String str = redisTemplate.opsForValue().get(cacheKey);
        //空值判断
        if (SysRedisConst.NULL_VAL.equals(str)) {
            return null;
        }
        Object obj = Jsons.toObj(str, new TypeReference<Object>() {
            @Override
            public Type getType() {
                return returnType;
            }
        });
        return obj;
    }

    /**
     * 布隆过滤器判断是否存在
     * @param bloomName  使用的布隆过滤器的名字
     * @param bloomValue   需要判定的数据
     * @return  返回是否存在
     */
    @Override
    public boolean bloomContains(String bloomName, Object bloomValue) {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(bloomName);
        return bloomFilter.contains(bloomValue);
    }

    /**
     * 尝试加锁
     * @param lockName  锁名
     * @return  是否加锁成功
     */
    @Override
    public boolean tryLock(String lockName) {
        RLock lock = redissonClient.getLock(lockName);
        return lock.tryLock();
    }

    /**
     * 释放锁
     * @param lockName 锁名
     */
    @Override
    public void unLock(String lockName) {
        RLock lock = redissonClient.getLock(lockName);
        lock.unlock();
    }

    /**
     * 缓存数据
     * @param cacheKey   缓存key
     * @param cacheData  数据
     * @param ttl    过期时间
     */
    @Override
    public void saveData(String cacheKey, Object cacheData, Long ttl) {
        if (cacheData == null) {
            redisTemplate.opsForValue().set(cacheKey,
                    SysRedisConst.NULL_VAL,
                    SysRedisConst.NULL_VAL_CACHETIME,
                    TimeUnit.SECONDS);
        }
        String str = Jsons.toStr(cacheData);
        redisTemplate.opsForValue().set(cacheKey,str,ttl,TimeUnit.SECONDS);
    }

    /**
     * 延迟双删
     * @param cacheKey  缓存key
     */
    @Override
    public void delay2Delete(String cacheKey) {
       redisTemplate.delete(cacheKey);
       //创建一个专门执行延迟队列的线程池
        scheduledThreadPool.schedule(() -> redisTemplate.delete(cacheKey),
                5,
                TimeUnit.SECONDS);
    }

}
