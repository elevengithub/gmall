package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory3;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author 14613
* @description 针对表【base_category3(三级分类表)】的数据库操作Service实现
* @createDate 2022-08-22 20:11:00
*/
@Service
public class BaseCategory3ServiceImpl
        extends ServiceImpl<BaseCategory3Mapper, BaseCategory3>
        implements BaseCategory3Service{

    @Resource
    BaseCategory3Mapper baseCategory3Mapper;

    @Override
    public List<BaseCategory3> getCategory3s(Long c2Id) {
        QueryWrapper<BaseCategory3> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category2_id",c2Id);
        List list = baseCategory3Mapper.selectList(queryWrapper);
        return list;
    }
}




