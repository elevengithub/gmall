package com.atguigu.gmall.feign.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.model.to.CategoryView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("service-product")
public interface SkuFeignClient {

//    @GetMapping("/api/inner/rpc/product/getSkuDetailTo/{skuId}")
//    Result<SkuDetailTo> getSkuDetailTo(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId获取sku商品信息
     * @param skuId
     * @return
     */
    @GetMapping("/api/inner/rpc/product/getSkuInfo/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId获取对应的skuImageList
     * @param skuId
     * @return
     */
    @GetMapping("/api/inner/rpc/product/getSkuImageList/{skuId}")
    public Result<List<SkuImage>> getSkuImageList(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId获取实时价格
     * @param skuId
     * @return
     */
    @GetMapping("/api/inner/rpc/product/getSkuPrice/{skuId}")
    public Result<BigDecimal> getSkuPrice(@PathVariable("skuId") Long skuId);

    /**
     * 根据category3Id获取商品对应的CategoryView三级分类信息
     * @param c3Id
     * @return
     */
    @GetMapping("/api/inner/rpc/product/getCategoryView/{c3Id}")
    public Result<CategoryView> getCategoryView(@PathVariable("c3Id") Long c3Id);

    /**
     * 获取该商品对应的所有销售属性名和值的spuSaleAttrList信息，并高亮显示已选择sku商品
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/api/inner/rpc/product/getSpuSaleAttrList/{skuId}/{spuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrList(@PathVariable("skuId") Long skuId,
                                                        @PathVariable("spuId") Long spuId);

    /**
     *根据spuId获取sku商品对应的所有兄弟商品信息
     * @param spuId
     * @return
     */
    @GetMapping("/api/inner/rpc/product/getValuesSkuJsonTo/{spuId}")
    public Result<String> getValuesSkuJsonTo(@PathVariable("spuId") Long spuId);

    /**
     * 获取三级分类信息
     * @return
     */
    @GetMapping("/api/inner/rpc/product/getCategoryTree")
    Result<List<CategoryTreeTo>> getCategoryTree();
}
