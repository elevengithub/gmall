package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuImage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 14613
* @description 针对表【sku_image(库存单元图片表)】的数据库操作Mapper
* @createDate 2022-08-23 11:50:21
* @Entity com.atguigu.gmall.model.product.SkuImage
*/
public interface SkuImageMapper extends BaseMapper<SkuImage> {

    List<SkuImage> getSkuImageList(@Param("skuId") Long skuId);
}




