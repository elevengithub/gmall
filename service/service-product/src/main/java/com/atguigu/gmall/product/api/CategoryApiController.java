package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/inner/rpc/product")
@RestController
public class CategoryApiController {

    @Autowired
    BaseCategory2Service baseCategory2Service;

    /**
     * 获取三级分类信息，首页展示
     * @return
     */
    @GetMapping("/getCategoryTree")
    public Result getCategoryTree(){
        List<CategoryTreeTo> list = baseCategory2Service.getCategoryTree();
        return Result.ok(list);
    }
}
