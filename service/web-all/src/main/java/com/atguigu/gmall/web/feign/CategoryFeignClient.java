package com.atguigu.gmall.web.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@RequestMapping("/api/inner/rpc/product")
@FeignClient("service-product")
public interface CategoryFeignClient {

    /**
     * 获取三级分类信息
     * @return
     */
    @GetMapping("/getCategoryTree")
    Result<List<CategoryTreeTo>> getCategoryTree();
}
