package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryView;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
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

    @Override
    public SkuDetailTo getSkuDetailTo(Long skuId) {
        SkuDetailTo skuDetailTo = new SkuDetailTo();
        //1、根据skuId获取skuInfo对象
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        //获取sku对应的skuImageList
        List<SkuImage> imageList = skuImageService.getSkuImageList(skuId);
        skuInfo.setSkuImageList(imageList);
        skuDetailTo.setSkuInfo(skuInfo);
        //2、获取商品的实时价格，设置到SkuDetailTo的price属性
        BigDecimal price = skuInfoMapper.get1010Price(skuId);
        skuDetailTo.setPrice(price);
        //3、获取SkuDetailTo中的categoryView，根据category3_id
        CategoryView categoryView = baseCategory3Service.getCategoryView(skuInfo.getCategory3Id());
        skuDetailTo.setCategoryView(categoryView);
        //4、获取该商品对应的所有销售属性名和值的spuSaleAttrList信息，并高亮显示已选择sku商品
        List<SpuSaleAttr> list = spuSaleAttrService.getSpuSaleAttrList(skuId,skuInfo.getSpuId());
        skuDetailTo.setSpuSaleAttrList(list);
        return skuDetailTo;
    }
}




