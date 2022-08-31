package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.to.ValuesSkuJsonTo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 14613
* @description 针对表【sku_sale_attr_value(sku销售属性值)】的数据库操作Service实现
* @createDate 2022-08-23 11:50:21
*/
@Service
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueMapper, SkuSaleAttrValue>
    implements SkuSaleAttrValueService{

    @Resource
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Override
    public String getValuesSkuJsonTo(Long spuId) {
        List<ValuesSkuJsonTo> list = skuSaleAttrValueMapper.getValuesSkuJsonTo(spuId);
        Map<String,Long> map = new HashMap<>();
        list.forEach(v -> {
            map.put(v.getValueJson(),v.getSkuId());
        });
        return Jsons.toStr(map);
    }
}




