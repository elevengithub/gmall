package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平台属性信息API
 */
@Slf4j
@RequestMapping("/admin/product")
@RestController
public class BaseAttrInfoController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    /**
     *根据一级、二级、三级分离id获取对应的属性信息集合
     * @param category1Id 一级分类id
     * @param category2Id 二级分类id
     * @param category3Id 三级分类id
     * @return
     */
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result getAttrInfoList(@PathVariable("category1Id") Long category1Id,
                                  @PathVariable("category2Id") Long category2Id,
                                  @PathVariable("category3Id") Long category3Id){
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoService.getAttrInfoList(category1Id,category2Id,category3Id);
        return Result.ok(baseAttrInfos);
    }

    /**
     * 新增或修改属性信息
     * @param baseAttrInfo  前端传来的AttrInfo信息
     * @return
     */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.saveOrUpdateAttrInfo(baseAttrInfo);
        return Result.ok();
    }
}
