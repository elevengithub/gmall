package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 14613
* @description 针对表【spu_info(商品表)】的数据库操作Service
* @createDate 2022-08-23 11:50:21
*/
public interface SpuInfoService extends IService<SpuInfo> {

    IPage<SpuInfo> getIPage(Page<SpuInfo> spuInfoPage, Long category3Id);

    void saveSpuInfo(SpuInfo spuInfo);
}
