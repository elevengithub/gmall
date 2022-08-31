package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryView;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.model.to.ValuesSkuJsonTo;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
* @author 14613
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2022-08-23 11:50:21
*/
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Resource
    SkuImageService skuImageService;
    @Resource
    SkuAttrValueService skuAttrValueService;
    @Resource
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Resource
    SkuInfoMapper skuInfoMapper;
    @Resource
    BaseCategory3Service baseCategory3Service;
    @Resource
    SpuSaleAttrService spuSaleAttrService;
    @Resource
    RedissonClient redissonClient;

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //1、保存skuInfo的基本信息到sku_info表中
        save(skuInfo);
        Long skuId = skuInfo.getId();
        //2、保存skuInfo中skuImageList信息到sku_image表
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        //2.1、回填sku_id
        skuImageList.forEach(skuImage -> skuImage.setSkuId(skuId));
        //2.2、批量保存
        skuImageService.saveBatch(skuImageList);
        //3、保存skuAttrValueList信息到sku_attr_value表
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        //3.1、回填sku_id
        skuAttrValueList.forEach(sav -> sav.setSkuId(skuId));
        //3.2、批量保存
        skuAttrValueService.saveBatch(skuAttrValueList);
        //4、保存skuSaleAttrValueList信息到sku_sale_attr_value表
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        //4.1、回填sku_id和spu_id
        skuSaleAttrValueList.forEach(ssav -> {
            ssav.setSkuId(skuId);
            ssav.setSpuId(skuInfo.getSpuId());
        });
        //4.2、批量保存
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueList);
        //5、将skuId保存至布隆过滤器中
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        filter.add(skuId);
    }

    @Override
    public void cancelSale(Long id) {
        //1、修改sku_info表中skuId的is_sale； 1上架  0下架
        skuInfoMapper.updateIsSale(id,0);
    }

    @Override
    public void onSale(Long id) {
        skuInfoMapper.updateIsSale(id,1);
    }

    /**
     * 根据skuId获取skuInfo商品信息
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo;
    }

    /**
     * 根据skuId获取对应的skuImageList
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImage> getSkuImageList(Long skuId) {
        List<SkuImage> imageList = skuImageService.getSkuImageList(skuId);
        return imageList;
    }

    /**
     * 根据skuId获取实时价格
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        BigDecimal price = skuInfoMapper.get1010Price(skuId);
        return price;
    }

    /**
     * 根据category3Id获取商品对应的CategoryView三级分类信息
     * @param c3Id
     * @return
     */
    @Override
    public CategoryView getCategoryView(Long c3Id) {
        CategoryView categoryView = baseCategory3Service.getCategoryView(c3Id);
        return categoryView;
    }

    /**
     * 获取该商品对应的所有销售属性名和值的spuSaleAttrList信息，并高亮显示已选择sku商品
     * @param skuId
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long skuId, Long spuId) {
        List<SpuSaleAttr> list = spuSaleAttrService.getSpuSaleAttrList(skuId,spuId);
        return list;
    }

    /**
     *根据spuId获取sku商品对应的所有兄弟商品信息
     * @param spuId
     * @return
     */
    @Override
    public String getValuesSkuJsonTo(Long spuId) {
        String json = skuSaleAttrValueService.getValuesSkuJsonTo(spuId);
        return json;
    }

    /**
     * 获取所有商品id
     * @return
     */
    @Override
    public List<Long> getSkuIdList() {
        List<Long> ids = skuInfoMapper.getSkuIdList();
        return ids;
    }


    /*@Override
    public SkuDetailTo getSkuDetailTo(Long skuId){
        SkuDetailTo skuDetailTo = new SkuDetailTo();
        //1、根据skuId获取skuInfo对象
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        //2、获取sku对应的skuImageList
        List<SkuImage> imageList = skuImageService.getSkuImageList(skuId);
        skuInfo.setSkuImageList(imageList);
        skuDetailTo.setSkuInfo(skuInfo);
        //3、获取商品的实时价格，设置到SkuDetailTo的price属性
        BigDecimal price = skuInfoMapper.get1010Price(skuId);
        skuDetailTo.setPrice(price);
        //4、获取SkuDetailTo中的categoryView，根据category3_id
        CategoryView categoryView = baseCategory3Service.getCategoryView(skuInfo.getCategory3Id());
        skuDetailTo.setCategoryView(categoryView);
        //5、获取该商品对应的所有销售属性名和值的spuSaleAttrList信息，并高亮显示已选择sku商品
        List<SpuSaleAttr> list = spuSaleAttrService.getSpuSaleAttrList(skuId,skuInfo.getSpuId());
        skuDetailTo.setSpuSaleAttrList(list);
        //6、根据当前skuId获取当前sku对应的所有兄弟商品信息。即：valuesSkuJson。以商品的属性为key，商品id为值的json字符串。
        //前端代码：JSON.parse() : Json 字符串转换为对象！ {"115|117":"44","114|117":"45"}
        String json = skuSaleAttrValueService.getValuesSkuJsonTo(skuInfo.getSpuId());
        skuDetailTo.setValuesSkuJson(json);
        return skuDetailTo;
    }*/
}




