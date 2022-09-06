package com.atguigu.gmall.search.service;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;

public interface GoodsService {
    void saveGoods(Goods goods);

    void deleteGoods(Long id);

    SearchResponseVo search(SearchParamVo searchParam);

    void updateHotScore(Long skuId, Long hotScore);
}
