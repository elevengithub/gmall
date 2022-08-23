package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.getAttrInfoList(category1Id, category2Id, category3Id);
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
            //1、修改属性信息
            baseAttrInfoMapper.updateById(baseAttrInfo);
            //获取BaseAttrInfo中新传到后台的对象中的List<BaseAttrValue>属性。
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            //2、修改属性值信息
            //attrValueList中的值有可能是发生了新增、删除和修改
            //2.1、先处理删除掉的属性值内容
            //从attrValueList获取到所有属性值的id集合
            List<Long> attrIdList = attrValueList.stream()
                    .map(attrValue -> attrValue.getId())
                    .collect(Collectors.toList());
            //删除数据库中id不在attrIdList集合中的属性
            if (attrIdList.size() > 0) {
                //部分删除
                QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("attr_id",baseAttrInfo.getId());
                queryWrapper.notIn("id",attrIdList);
                baseAttrValueMapper.delete(queryWrapper);
            }else if(attrIdList.size() == 0){
                //全部删除
                QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("attr_id",baseAttrInfo.getId());
                baseAttrValueMapper.delete(queryWrapper);
            }
            //2.2、处理新增和修改的属性值
            attrValueList.forEach(attrValue -> {
                if (attrValue.getId() == null) {
                    //id为空进行新增操作
                    attrValue.setAttrId(baseAttrInfo.getId());
                    baseAttrValueMapper.insert(attrValue);
                } else {
                    //存在id进行修改操作
                    baseAttrValueMapper.updateById(attrValue);
                }
            });
        }
    }
}




