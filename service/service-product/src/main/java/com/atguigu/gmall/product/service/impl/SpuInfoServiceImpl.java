package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 14613
 * @description 针对表【spu_info(商品表)】的数据库操作Service实现
 * @createDate 2022-08-23 11:50:21
 */
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
        implements SpuInfoService {

    @Resource
    SpuInfoMapper spuInfoMapper;
    @Resource
    SpuImageService spuImageService;
    @Resource
    SpuSaleAttrService spuSaleAttrService;
    @Resource
    SpuSaleAttrValueService spuSaleAttrValueService;

    @Override
    public IPage<SpuInfo> getIPage(Page<SpuInfo> spuInfoPage, Long category3Id) {
        LambdaQueryWrapper<SpuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpuInfo::getCategory3Id, category3Id);
        return spuInfoMapper.selectPage(spuInfoPage, wrapper);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //1、保存spu的基本信息进spu_info表
        spuInfoMapper.insert(spuInfo);
        Long spuId = spuInfo.getId();
        //2、获取spu的图片信息存入数据库表spu_image
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        //回填spu_id
        spuImageList.forEach(spuImage -> spuImage.setSpuId(spuId));
        spuImageService.saveBatch(spuImageList);
        //3、获取spu的销售属性集合信息存入销售属性表spu_sale_attr
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        spuSaleAttrList.forEach(spuSaleAttr -> {
            //回填spu_id
            spuSaleAttr.setSpuId(spuId);
            //获取spu销售属性名中的销售属性值信息存入数据库表spu_sale_attr_value
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            spuSaleAttrValueList.forEach(attrValue -> {
                //回填spu_id
                attrValue.setSpuId(spuId);
                //回填sale_attr_name
                attrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
            });
            //批量保存spu的销售属性值信息进spu_sale_attr_value
            spuSaleAttrValueService.saveBatch(spuSaleAttrValueList);
        });
        //批量保存spu的销售属性信息进spu_sale_attr
        spuSaleAttrService.saveBatch(spuSaleAttrList);
    }
}




