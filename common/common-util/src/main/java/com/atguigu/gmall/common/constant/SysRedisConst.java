package com.atguigu.gmall.common.constant;

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
    public static final String USERID_HEADER = "userid";
    public static final String USERTEMPID_HEADER = "usertempid";
    public static final String CART_KRY = "cart:user:";
    public static final long CART_ITEMS_LIMIT = 200;
    public static final Integer CART_ITEM_NUM_LIMIT = 200;
}
