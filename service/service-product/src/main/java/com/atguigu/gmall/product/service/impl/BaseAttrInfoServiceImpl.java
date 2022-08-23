package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 14613
 * @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
 * @createDate 2022-08-22 21:17:11
 */
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
        implements BaseAttrInfoService {

    @Resource
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Resource
    BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.getAttrInfoList(category1Id, category1Id, category3Id);
        return baseAttrInfos;
    }

    @Override
    public void saveOrUpdateAttrInfo(BaseAttrInfo baseAttrInfo) {

        //根据前端传递的BaseAttrInfo中的id是否为空，如果为空代表新增，如果不为空，代表修改。
        if (baseAttrInfo.getId() == null) {
            //新增操作
            baseAttrInfoMapper.insert(baseAttrInfo);
            //获取到新增之后的自增id
            Long id = baseAttrInfo.getId();
            //获取BaseAttrInfo中的属性值集合，存入属性值表
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            attrValueList.forEach(attrValue -> {
                //将base_attr_info的id属性赋值给base_attr_value的attr_id
                attrValue.setAttrId(id);
                //新增属性值
                baseAttrValueMapper.insert(attrValue);
            });
        } else {
            //修改属性信息
            baseAttrInfoMapper.updateById(baseAttrInfo);
        }
    }


}




