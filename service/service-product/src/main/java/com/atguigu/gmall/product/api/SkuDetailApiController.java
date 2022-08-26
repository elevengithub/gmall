package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/inner/rpc/product")
@RestController
public class SkuDetailApiController {

    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 获取商品详情
     * @param skuId 商品id
     * @return
     */
    @GetMapping("/getSkuDetailTo/{skuId}")
    public Result<SkuDetailTo> getSkuDetailTo(@PathVariable("skuId") Long skuId){
        SkuDetailTo skuDetailTo = skuInfoService.getSkuDetailTo(skuId);
        return Result.ok(skuDetailTo);
    }
}
