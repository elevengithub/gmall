package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
}




