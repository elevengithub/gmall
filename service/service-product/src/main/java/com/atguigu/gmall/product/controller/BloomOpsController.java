package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import com.atguigu.starter.cache.constant.SysRedisConst;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/admin/product")
@RestController
public class BloomOpsController {

    @Resource
    BloomOpsService bloomOpsService;
    @Resource
    BloomDataQueryService bloomDataQueryService;

    /**
     * 重建布隆过滤器
     * @return
     */
    @GetMapping("/rebuild/sku/now")
    public Result rebuildBloom(){
        String bloomName = SysRedisConst.BLOOM_SKUID;
        bloomOpsService.rebuildBloom(bloomName,bloomDataQueryService);
        return Result.ok();
    }
}
