package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryView;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.model.to.ValuesSkuJsonTo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.math.BigDecimal;
import java.util.List;

/**
* @author 14613
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2022-08-23 11:50:21
*/
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfo(SkuInfo skuInfo);

    void cancelSale(Long id);

    void onSale(Long id);

//    SkuDetailTo getSkuDetailTo(Long skuId);

    /**
     * 根据skuId获取skuInfo商品信息
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * 根据skuId获取对应的skuImageList
     * @param skuId
     * @return
     */
    List<SkuImage> getSkuImageList(Long skuId);

    /**
     * 根据skuId获取实时价格
     * @param skuId
     * @return
     */
    BigDecimal getSkuPrice(Long skuId);

    /**
     * 根据category3Id获取商品对应的CategoryView三级分类信息
     * @param c3Id
     * @return
     */
    CategoryView getCategoryView(Long c3Id);

    /**
     * 获取该商品对应的所有销售属性名和值的spuSaleAttrList信息，并高亮显示已选择sku商品
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(Long skuId, Long spuId);

    /**
     *根据spuId获取sku商品对应的所有兄弟商品信息
     * @param spuId
     * @return
     */
    String getValuesSkuJsonTo(Long spuId);
}
