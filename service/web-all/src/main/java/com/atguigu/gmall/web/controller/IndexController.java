package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @Autowired
    SkuFeignClient categoryFeignClient;

    /**
     * 跳到首页。获取三级分类信息，首页详情展示
     * @return
     */
    @GetMapping({"/","/index"})
    public String getCategoryTree(Model model){
        Result result = categoryFeignClient.getCategoryTree();
        model.addAttribute("list",result.getData());
        return "index/index";
    }
}
