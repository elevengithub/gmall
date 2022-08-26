package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 14613
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2022-08-23 11:50:21
*/
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfo(SkuInfo skuInfo);

    void cancelSale(Long id);

    void onSale(Long id);

    SkuDetailTo getSkuDetailTo(Long skuId);
}
