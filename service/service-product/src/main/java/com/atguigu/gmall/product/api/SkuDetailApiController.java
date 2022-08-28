package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryView;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.model.to.ValuesSkuJsonTo;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

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
    /*@GetMapping("/getSkuDetailTo/{skuId}")
    public Result<SkuDetailTo> getSkuDetailTo(@PathVariable("skuId") Long skuId){
        SkuDetailTo skuDetailTo = skuInfoService.getSkuDetailTo(skuId);
        return Result.ok(skuDetailTo);
    }*/

    /**
     * 根据skuId获取sku商品信息
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfo/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId){
        SkuInfo skuInfo = skuInfoService.getSkuInfo(skuId);
        return Result.ok(skuInfo);
    }

    /**
     * 根据skuId获取对应的skuImageList
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuImageList/{skuId}")
    public Result<List<SkuImage>> getSkuImageList(@PathVariable("skuId") Long skuId){
        List<SkuImage> list = skuInfoService.getSkuImageList(skuId);
        return Result.ok(list);
    }

    /**
     * 根据skuId获取实时价格
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuPrice/{skuId}")
    public Result<BigDecimal> getSkuPrice(@PathVariable("skuId") Long skuId){
        BigDecimal price = skuInfoService.getSkuPrice(skuId);
        return Result.ok(price);
    }

    /**
     * 根据category3Id获取商品对应的CategoryView三级分类信息
     * @param c3Id
     * @return
     */
    @GetMapping("/getCategoryView/{c3Id}")
    public Result<CategoryView> getCategoryView(@PathVariable("c3Id") Long c3Id){
        CategoryView categoryView = skuInfoService.getCategoryView(c3Id);
        return Result.ok(categoryView);
    }

    /**
     * 获取该商品对应的所有销售属性名和值的spuSaleAttrList信息，并高亮显示已选择sku商品
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/getSpuSaleAttrList/{skuId}/{spuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrList(@PathVariable("skuId") Long skuId,
                                                        @PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> list = skuInfoService.getSpuSaleAttrList(skuId,spuId);
        return Result.ok(list);
    }

    /**
     *根据spuId获取sku商品对应的所有兄弟商品信息
     * @param spuId
     * @return
     */
    @GetMapping("/getValuesSkuJsonTo/{spuId}")
    public Result<String> getValuesSkuJsonTo(@PathVariable("spuId") Long spuId){
        String str = skuInfoService.getValuesSkuJsonTo(spuId);
        return Result.ok(str);
    }
}
