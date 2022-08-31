package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
* @author 14613
* @description 针对表【sku_info(库存单元表)】的数据库操作Mapper
* @createDate 2022-08-23 11:50:21
* @Entity com.atguigu.gmall.model.product.SkuInfo
*/
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    void updateIsSale(@Param("id") Long id, @Param("sale") Integer sale);

    BigDecimal get1010Price(@Param("skuId") Long skuId);

    List<Long> getSkuIdList();
}




