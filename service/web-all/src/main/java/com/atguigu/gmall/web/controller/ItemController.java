package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.web.feign.SkuDetailFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    /**
     * 获取商品详情信息
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String getSkuDetailTo(@PathVariable("skuId") Long skuId, Model model){
        Result<SkuDetailTo> result = skuDetailFeignClient.getSkuDetailTo(skuId);
        SkuDetailTo skuDetailTo = result.getData();
        model.addAttribute("categoryView",skuDetailTo.getCategoryView());
        model.addAttribute("skuInfo",skuDetailTo.getSkuInfo());
        model.addAttribute("price",skuDetailTo.getPrice());
        model.addAttribute("spuSaleAttrList",skuDetailTo.getSpuSaleAttrList());
        return "item/index";
    }
}
