package com.atguigu.starter.cache.service;


import java.lang.reflect.Type;

public interface CacheOpsService {
    /**
     * 查询缓存
     * @param cacheKey  缓存时的key值
     * @param clazz   缓存的value值类型
     * @return    返回缓存对象
     */
    <T>T getCacheData(String cacheKey, Class<T> clazz);

    /**
     * 布隆过滤器判定数据是否存在
     * @param skuId  要判断的id
     * @return  返回是否存在
     */
    boolean bloomContains(Long skuId);

    /**
     * 加锁
     * @param skuId  需要加锁的id
     * @return
     */
    boolean tryLock(Long skuId);

    /**
     * 将数据放入缓存
     * @param cacheKey   缓存key值
     * @param obj     缓存的对象
     */
    void saveData(String cacheKey, Object obj);

    /**
     * 根据商品id释放锁
     * @param skuId  商品id
     */
    void unLock(Long skuId);

    /**
     * 查询缓存
     * @param cacheKey  查询缓存使用的cacheKey
     * @param returnType  查询缓存结束后返回结果的类型
     * @return
     */
    Object getCacheData(String cacheKey, Type returnType);

    /**
     * 布隆过滤器判断是否存在
     * @param bloomName  使用的布隆过滤器的名字
     * @param bloomValue   需要判定的数据
     * @return  返回是否存在
     */
    boolean bloomContains(String bloomName,Object bloomValue);

    /**
     * 尝试加锁
     * @param lockName  锁名
     * @return  是否加锁成功
     */
    boolean tryLock(String lockName);

    /**
     * 释放锁
     * @param lockName   锁名
     * @return
     */
    void unLock(String lockName);

    /**
     * 缓存数据
     * @param cacheKey   缓存key
     * @param cacheData  数据
     * @param ttl    过期时间
     */
    void saveData(String cacheKey,Object cacheData,Long ttl);

    /**
     * 延迟双删
     * @param cacheKey  缓存key
     */
    void delay2Delete(String cacheKey);
}
