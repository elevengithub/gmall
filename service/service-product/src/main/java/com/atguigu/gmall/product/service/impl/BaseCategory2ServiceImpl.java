package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author 14613
* @description 针对表【base_category2(二级分类表)】的数据库操作Service实现
* @createDate 2022-08-22 20:11:00
*/
@Service
public class BaseCategory2ServiceImpl
        extends ServiceImpl<BaseCategory2Mapper, BaseCategory2>
        implements BaseCategory2Service{

    @Resource
    BaseCategory2Mapper baseCategory2Mapper;

    @Override
    public List<BaseCategory2> getCategory2s(Long c1Id) {
        List<BaseCategory2> category2s = baseCategory2Mapper.selectList(new LambdaQueryWrapper<BaseCategory2>()
                .eq(BaseCategory2::getCategory1Id, c1Id));
        return category2s;
    }
}




