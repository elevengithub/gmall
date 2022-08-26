package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/inner/rpc/item")
@RestController
public class SkuDetailController {

    @Autowired
    SkuDetailService skuDetailService;

    /**
     * 获取商品详情
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuDetailTo/{skuId}")
    public Result<SkuDetailTo> getSkuDetailTo(@PathVariable("skuId") Long skuId){
        SkuDetailTo skuDetailTo = skuDetailService.getSkuDetailTo(skuId);
        return Result.ok(skuDetailTo);
    }
}
