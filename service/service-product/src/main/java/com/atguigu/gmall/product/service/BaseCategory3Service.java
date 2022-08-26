package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.to.CategoryView;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 14613
* @description 针对表【base_category3(三级分类表)】的数据库操作Service
* @createDate 2022-08-22 20:11:00
*/
public interface BaseCategory3Service extends IService<BaseCategory3> {

    List<BaseCategory3> getCategory3s(Long c2Id);

    CategoryView getCategoryView(Long category3Id);
}
