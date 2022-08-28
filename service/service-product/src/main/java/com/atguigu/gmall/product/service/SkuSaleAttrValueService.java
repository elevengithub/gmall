package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
* @author 14613
* @description 针对表【sku_sale_attr_value(sku销售属性值)】的数据库操作Service
* @createDate 2022-08-23 11:50:21
*/
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValue> {

    String getValuesSkuJsonTo(Long spuId);
}
