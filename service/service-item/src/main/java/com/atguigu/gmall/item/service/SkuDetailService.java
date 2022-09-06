package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.to.SkuDetailTo;

public interface SkuDetailService {
    /**
     * 获取商品详情
     * @param skuId
     * @return
     */
    SkuDetailTo getSkuDetailTo(Long skuId);

    /**
     * 更新商品热度分
     * @param skuId
     */
    void updateHotScore(Long skuId);
}
