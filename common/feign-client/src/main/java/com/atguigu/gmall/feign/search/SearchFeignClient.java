package com.atguigu.gmall.feign.search;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/inner/rpc/search")
@FeignClient("service-search")
public interface SearchFeignClient {

    /**
     * 根据商品id删除es对应的文档
     * @param id  商品id
     * @return
     */
    @GetMapping("/goods/{id}")
    Result deleteGoods(@PathVariable("id") Long id);

    /**
     * 保存商品信息到es
     * @param goods
     * @return
     */
    @PostMapping("/goods")
    Result saveGoods(@RequestBody Goods goods);

    @PostMapping("/goods/search")
    Result<SearchResponseVo> search(@RequestBody SearchParamVo searchParam);

    /**
     * 更新商品的热度分
     * @param skuId
     * @param hotScore
     */
    @GetMapping("/goods/hotscore/{skuId}")
    Result updateHotScore(@PathVariable("skuId") Long skuId,
                                 @RequestParam("hotScore") Long hotScore);
}
