package com.atguigu.gmall.web.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-item")
public interface SkuDetailFeignClient {

    @GetMapping("/api/inner/rpc/item/getSkuDetailTo/{skuId}")
    Result<SkuDetailTo> getSkuDetailTo(@PathVariable("skuId") Long skuId);
}
