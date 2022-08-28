package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.config.threadpool.AppThreadPoolAutoConfiguration;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryView;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;
    @Autowired
    AppThreadPoolAutoConfiguration threadPool;

    /**
     * 使用异步编排根据skuId获取sku商品详情
     * @param skuId
     * @return
     */
    @Override
    public SkuDetailTo getSkuDetailTo(Long skuId) {
//        return getDetailToMethod1(skuId);
        ThreadPoolExecutor executor = threadPool.getThreadPoolExecutor();
        SkuDetailTo skuDetailTo = new SkuDetailTo();
        //1、获取skuInfo信息添加到skuDetailTo中
        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            Result<SkuInfo> skuInfoResult = skuDetailFeignClient.getSkuInfo(skuId);
            SkuInfo skuInfo = skuInfoResult.getData();
            skuDetailTo.setSkuInfo(skuInfo);
            return skuInfo;
        },executor);

        //2、获取sku图片信息添加到skuInfo中
        CompletableFuture<Void> skuImageFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<List<SkuImage>> skuImageListResult = skuDetailFeignClient.getSkuImageList(skuId);
            List<SkuImage> skuImageList = skuImageListResult.getData();
            skuInfo.setSkuImageList(skuImageList);
        }, executor);

        //3、获取sku实时价格添加到skuDetailTo中
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            Result<BigDecimal> skuPriceResult = skuDetailFeignClient.getSkuPrice(skuId);
            BigDecimal price = skuPriceResult.getData();
            skuDetailTo.setPrice(price);
        }, executor);

        //4、获取categoryView三级分类信息添加到skuDetailTo中
        CompletableFuture<Void> categoryViewFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<CategoryView> categoryViewResult = skuDetailFeignClient.getCategoryView(skuInfo.getCategory3Id());
            CategoryView categoryView = categoryViewResult.getData();
            skuDetailTo.setCategoryView(categoryView);
        }, executor);

        //5、获取sku销售属性名和值集合添加到skuDetailTo中
        CompletableFuture<Void> spuSaleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<List<SpuSaleAttr>> listResult = skuDetailFeignClient.getSpuSaleAttrList(skuId, skuInfo.getSpuId());
            List<SpuSaleAttr> spuSaleAttrList = listResult.getData();
            skuDetailTo.setSpuSaleAttrList(spuSaleAttrList);
        }, executor);

        //6、获取valuesSkuJson信息添加到skuDetailTo中
        CompletableFuture<Void> valuesSkuJsonFuture = skuInfoFuture.thenAcceptAsync(skuInfo ->{
            Result<String> valuesSkuJsonToResult = skuDetailFeignClient.getValuesSkuJsonTo(skuInfo.getSpuId());
            String valuesSkuJsonToStr = valuesSkuJsonToResult.getData();
            skuDetailTo.setValuesSkuJson(valuesSkuJsonToStr);
        },executor);

        //7、保证以上6个任务都完成之后再返回skuDetailTo
        CompletableFuture
                .allOf(skuImageFuture,priceFuture,categoryViewFuture,spuSaleAttrFuture,valuesSkuJsonFuture)
                .join();
        return skuDetailTo;
    }

    private SkuDetailTo getDetailToMethod1(Long skuId) {
        SkuDetailTo skuDetailTo = new SkuDetailTo();
        //1、添加skuInfo信息到skuDetailTo中
        Result<SkuInfo> skuInfoResult = skuDetailFeignClient.getSkuInfo(skuId);
        SkuInfo skuInfo = skuInfoResult.getData();
        skuDetailTo.setSkuInfo(skuInfo);
        //2、添加sku图片信息到skuInfo中
        Result<List<SkuImage>> skuImageListResult = skuDetailFeignClient.getSkuImageList(skuId);
        List<SkuImage> skuImageList = skuImageListResult.getData();
        skuInfo.setSkuImageList(skuImageList);
        //3、添加sku实时价格到skuDetailTo中
        Result<BigDecimal> skuPriceResult = skuDetailFeignClient.getSkuPrice(skuId);
        BigDecimal price = skuPriceResult.getData();
        skuDetailTo.setPrice(price);
        //4、添加categoryView三级分类信息到skuDetailTo中
        Result<CategoryView> categoryViewResult = skuDetailFeignClient.getCategoryView(skuInfo.getCategory3Id());
        CategoryView categoryView = categoryViewResult.getData();
        skuDetailTo.setCategoryView(categoryView);
        //5、添加sku销售属性名和值集合到skuDetailTo中
        Result<List<SpuSaleAttr>> listResult = skuDetailFeignClient.getSpuSaleAttrList(skuId, skuInfo.getSpuId());
        List<SpuSaleAttr> spuSaleAttrList = listResult.getData();
        skuDetailTo.setSpuSaleAttrList(spuSaleAttrList);
        //6、添加valuesSkuJson到skuDetailTo中
        Result<String> valuesSkuJsonToResult = skuDetailFeignClient.getValuesSkuJsonTo(skuInfo.getSpuId());
        String valuesSkuJsonToStr = valuesSkuJsonToResult.getData();
        skuDetailTo.setValuesSkuJson(valuesSkuJsonToStr);
        return skuDetailTo;
    }
}
