package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.item.cache.CacheOpsService;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryView;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.starter.cache.annotation.GmallCache;
import com.atguigu.starter.cache.constant.SysRedisConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuFeignClient skuDetailFeignClient;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    CacheOpsService cacheOpsService;
    @Autowired
    SearchFeignClient searchFeignClient;

    private Map<String,SkuDetailTo> cacheData = new ConcurrentHashMap<>();

    /**
     * 使用异步编排根据skuId获取sku商品详情
     * @param skuId
     * @return
     */
    @GmallCache(
            cacheKey = SysRedisConst.CACHE_SKU_PREFIX + "#{#params[0]}",
            bloomName = SysRedisConst.BLOOM_SKUID,
            bloomValue = "#{#params[0]}",
            lockName = SysRedisConst.LOCK_SKU_DETAIL + "#{#params[0]}",
            ttl = 60 * 60 * 24 * 7
    )
    @Override
    public SkuDetailTo getSkuDetailTo(Long skuId) {
        SkuDetailTo skuDetailTo = getDetailToMethod2(skuId);
        return skuDetailTo;
    }

    /**
     * 更新商品热度分
     * @param skuId  商品id
     */
    @Override
    public void updateHotScore(Long skuId) {
        Long hotScore = redisTemplate
                .opsForValue()
                .increment(SysRedisConst.SKU_HOTSCORE_PREFIX + skuId);
        if (hotScore % 100 == 0) {
            searchFeignClient.updateHotScore(skuId,hotScore);
        }
    }

    /**
     * 使用Redisson、分布式锁、布隆过滤器解决缓存三大问题
     * @param skuId
     * @return
     */
    private SkuDetailTo getDetailToMethod3(Long skuId) {
        //使用分布式缓存
        //1、查询缓存
        String cacheKey = SysRedisConst.CACHE_SKU_PREFIX + skuId;
        SkuDetailTo cacheData = cacheOpsService.getCacheData(cacheKey,SkuDetailTo.class);
        //1.1、缓存未命中
        if (cacheData == null) {
            //2、问布隆过滤器是否存在
            boolean isContains = cacheOpsService.bloomContains(skuId);
            //2.1、布隆过滤器判断不存在
            if (!isContains) {
                return null;
            }
            //2.2、布隆过滤器查询存在
            //3、加锁
            boolean isLocked = cacheOpsService.tryLock(skuId);
            //3.1、加锁成功
            if (isLocked) {
                //4、回源(布隆说存在，不一定存在)
                SkuDetailTo skuDetailTo = getDetailToMethod2(skuId);
                //5、放入缓存
                cacheOpsService.saveData(cacheKey,skuDetailTo);
                //6、释放锁
                cacheOpsService.unLock(skuId);
                //7、返回数据
                return skuDetailTo;
            }
            //3.2、加锁失败，睡眠一秒，返回直查缓存
            try {
                Thread.sleep(1000);
                cacheOpsService.getCacheData(cacheKey,SkuDetailTo.class);
            } catch (InterruptedException e) {

            }
        }
        //1.2缓存命中，直接返回
        return cacheData;
    }

    private SkuDetailTo mapCache(Long skuId) {
        //使用本地缓存
        //查询缓存是否命中
        SkuDetailTo skuDetailTo = cacheData.get("skuData");
        if (skuDetailTo != null) {
            //缓存命中，直接返回
            return skuDetailTo;
        } else {
            //缓存未命中，回源
            skuDetailTo = getDetailToMethod2(skuId);
            //回源之后将查询到的数据放入缓存
            cacheData.put("skuData",skuDetailTo);
            return skuDetailTo;
        }
    }

    /**
     * 使用异步编排根据skuId获取sku商品详情
     * @param skuId
     * @return
     */
    private SkuDetailTo getDetailToMethod2(Long skuId) {
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
            if (skuInfo != null) {
                Result<List<SkuImage>> skuImageListResult = skuDetailFeignClient.getSkuImageList(skuId);
                List<SkuImage> skuImageList = skuImageListResult.getData();
                skuInfo.setSkuImageList(skuImageList);
            }
        }, executor);

        //3、获取sku实时价格添加到skuDetailTo中
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            Result<BigDecimal> skuPriceResult = skuDetailFeignClient.getSkuPrice(skuId);
            BigDecimal price = skuPriceResult.getData();
            skuDetailTo.setPrice(price);
        }, executor);

        //4、获取categoryView三级分类信息添加到skuDetailTo中
        CompletableFuture<Void> categoryViewFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo != null) {
                Result<CategoryView> categoryViewResult = skuDetailFeignClient.getCategoryView(skuInfo.getCategory3Id());
                CategoryView categoryView = categoryViewResult.getData();
                skuDetailTo.setCategoryView(categoryView);
            }
        }, executor);

        //5、获取sku销售属性名和值集合添加到skuDetailTo中
        CompletableFuture<Void> spuSaleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo != null) {
                Result<List<SpuSaleAttr>> listResult = skuDetailFeignClient.getSpuSaleAttrList(skuId, skuInfo.getSpuId());
                List<SpuSaleAttr> spuSaleAttrList = listResult.getData();
                skuDetailTo.setSpuSaleAttrList(spuSaleAttrList);
            }
        }, executor);

        //6、获取valuesSkuJson信息添加到skuDetailTo中
        CompletableFuture<Void> valuesSkuJsonFuture = skuInfoFuture.thenAcceptAsync(skuInfo ->{
            if (skuInfo != null) {
                Result<String> valuesSkuJsonToResult = skuDetailFeignClient.getValuesSkuJsonTo(skuInfo.getSpuId());
                String valuesSkuJsonToStr = valuesSkuJsonToResult.getData();
                skuDetailTo.setValuesSkuJson(valuesSkuJsonToStr);
            }
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
