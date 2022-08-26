package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.to.CategoryView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author 14613
* @description 针对表【base_category3(三级分类表)】的数据库操作Mapper
* @createDate 2022-08-22 20:11:00
* @Entity com.atguigu.gmall.product.domain.BaseCategory3
*/
public interface BaseCategory3Mapper extends BaseMapper<BaseCategory3> {

    CategoryView getCategoryView(@Param("category3Id") Long category3Id);
}




