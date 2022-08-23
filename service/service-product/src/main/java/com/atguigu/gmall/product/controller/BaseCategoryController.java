package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 后台管理商品信息分类API
 */
@RequestMapping("/admin/product")
@RestController
public class BaseCategoryController {

    @Resource
    BaseCategory1Service baseCategory1Service;
    @Resource
    BaseCategory2Service baseCategory2Service;
    @Resource
    BaseCategory3Service baseCategory3Service;

    /**
     * 获取所有一级分类
     * @return
     */
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> category1s = baseCategory1Service.list();
        return Result.ok(category1s);
    }

    /**
     * 根据一级分类id获取所有二级分类
     * @return
     */
    @GetMapping("/getCategory2/{c1Id}")
    public Result getCategory2(@PathVariable("c1Id") Long c1Id){
        List<BaseCategory2> category2s = baseCategory2Service.getCategory2s(c1Id);
        return Result.ok(category2s);
    }

    /**
     * 根据二级分类id获取所有三级分类
     * @return
     */
    @GetMapping("/getCategory3/{c2Id}")
    public Result getCategory3(@PathVariable("c2Id") Long c2Id){
        List<BaseCategory3> category3s = baseCategory3Service.getCategory3s(c2Id);
        return Result.ok(category3s);
    }
}
