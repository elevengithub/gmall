package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/product")
@RestController
public class SpuController {

    @Autowired
    SpuInfoService spuInfoService;
    @Autowired
    BaseSaleAttrService baseSaleAttrService;
    @Autowired
    SpuImageService spuImageService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    /**
     * 获取spu分页列表
     * @param page   当前页数
     * @param limit  每页显示条数
     * @param category3Id  三级分类id
     * @return
     */
    @GetMapping("/{page}/{limit}")
    public Result getSPUPage(@PathVariable("page") Long page,
                             @PathVariable("limit") Long limit,
                             @RequestParam("category3Id") Long category3Id){
        Page<SpuInfo> spuInfoPage = new Page<>(page,limit);
        IPage<SpuInfo> spuInfoIPage = spuInfoService.getIPage(spuInfoPage,category3Id);
        return Result.ok(spuInfoIPage);
    }

    /**
     * 获取销售属性集合
     * @return
     */
    @GetMapping("/baseSaleAttrList")
    public Result getBaseSaleAttrList(){
        return Result.ok(baseSaleAttrService.list());
    }

    /**
     * 新增SPU
     * @param spuInfo  前端提交的spu对象
     * @return
     */
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuInfoService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    /**
     * 根据spuId查询spu图片集合
     * @param spuId
     * @return
     */
    @GetMapping("/spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable("spuId") Long spuId){
        List<SpuImage> list = spuImageService.list(new LambdaQueryWrapper<SpuImage>()
                .eq(SpuImage::getSpuId, spuId));
        return Result.ok(list);
    }

    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> list = spuSaleAttrService.getSaleAttr(spuId);
        return Result.ok(list);
    }
}
