package com.atguigu.gmall.feign.cart;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/inner/rpc/cart")
@FeignClient("service-cart")
public interface CartFeignClient {

    /**
     * 添加商品到购物车
     * @param skuId  商品id
     * @param skuNum  商品数量
     * @return
     */
    @GetMapping("/addCart")
    Result<SkuInfo> addCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("skuNum") Integer skuNum);
}
