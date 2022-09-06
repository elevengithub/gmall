package com.atguigu.starter.cache.constant;

public class SysRedisConst {
    public static final String CACHE_SKU_PREFIX = "sku:data:";
    public static final String NULL_VAL = "x";
    public static final String LOCK_SKU_DETAIL = "lock:sku:detail:";
    public static final long NULL_VAL_CACHETIME = 60 * 30;
    public static final long VAL_CACHETIME = 60 * 60 * 24 * 7;
    public static final String BLOOM_SKUID = "bloom:sku";
    public static final String CACHE_CATEGORYS = "categorys";
    public static final String USER_LOGIN = "user:login:";
    public static final String SKU_HOTSCORE_PREFIX = "sku:hotscore:";
}
