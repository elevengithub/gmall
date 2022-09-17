package com.atguigu.gmall.feign.ware.fallback;

import com.atguigu.gmall.feign.ware.WareFeignClient;

public class WareFallback implements WareFeignClient {
    @Override
    public String hasStock(Long skuId, Integer num) {
        //显示有货
        return "1";
    }
}
