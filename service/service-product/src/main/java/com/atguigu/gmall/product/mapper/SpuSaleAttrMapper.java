package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 14613
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Mapper
* @createDate 2022-08-23 11:50:21
* @Entity com.atguigu.gmall.model.product.SpuSaleAttr
*/
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    List<SpuSaleAttr> getSaleAttr(@Param("spuId") Long spuId);

    List<SpuSaleAttr> getSpuSaleAttrList(@Param("skuId") Long skuId, @Param("spuId") Long spuId);
}




