package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/product")
@RestController
public class SkuController {

    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 分页查询sku列表
     * @param page   第几页
     * @param limit  每页显示多少条
     * @return
     */
    @GetMapping("/list/{page}/{limit}")
    public Result getSkuPage(@PathVariable("page") Long page,
                             @PathVariable("limit") Long limit){
        Page<SkuInfo> skuInfoPage = new Page<>(page,limit);
        return Result.ok(skuInfoService.page(skuInfoPage));
    }

    /**
     * 新增sku
     * @param skuInfo
     * @return
     */
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        skuInfoService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    /**
     * 下架sku
     * @param id
     * @return
     */
    @GetMapping("/cancelSale/{id}")
    public Result cancelSale(@PathVariable("id") Long id){
        skuInfoService.cancelSale(id);
        return Result.ok();
    }

    @GetMapping("/onSale/{id}")
    public Result onSale(@PathVariable("id") Long id){
        skuInfoService.onSale(id);
        return Result.ok();
    }
}
