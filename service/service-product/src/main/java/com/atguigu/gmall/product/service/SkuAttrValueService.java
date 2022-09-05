package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 14613
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Service
* @createDate 2022-08-23 11:50:21
*/
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    /**
     * 根据skuId获取对应属性名和值
     * @param id  商品id
     * @return
     */
    List<SearchAttr> getSearchAttrs(Long id);
}
