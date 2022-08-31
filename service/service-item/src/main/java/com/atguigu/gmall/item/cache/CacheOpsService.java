package com.atguigu.gmall.item.cache;


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
}
