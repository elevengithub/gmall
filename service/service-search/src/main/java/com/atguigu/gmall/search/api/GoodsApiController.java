package com.atguigu.gmall.search.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/inner/rpc/search")
@RestController
public class GoodsApiController {

    @Autowired
    GoodsService goodsService;

    /**
     * 保存商品信息到es
     * @param goods
     * @return
     */
    @PostMapping("/goods")
    Result saveGoods(@RequestBody Goods goods){
        goodsService.saveGoods(goods);
        return Result.ok();
    }

    /**
     * 根据商品id删除es对应的文档
     * @param id  商品id
     * @return
     */
    @GetMapping("/goods/{id}")
    public Result deleteGoods(@PathVariable("id") Long id){
        goodsService.deleteGoods(id);
        return Result.ok();
    }

    /**
     * 根据搜索条件检索商品
     * @param searchParam
     * @return
     */
    @PostMapping("/goods/search")
    public Result<SearchResponseVo> search(@RequestBody SearchParamVo searchParam){
        SearchResponseVo responseVo = goodsService.search(searchParam);
        return Result.ok(responseVo);
    }
}
