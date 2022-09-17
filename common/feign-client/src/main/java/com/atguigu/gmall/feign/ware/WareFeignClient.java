package com.atguigu.gmall.feign.ware;

import com.atguigu.gmall.feign.ware.fallback.WareFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "ware-manage",url = "${app.ware-url:http://localhost:9001/}",fallback = WareFallback.class)
public interface WareFeignClient {

    //根据sku判断是否有库存
    @RequestMapping("hasStock")
    String hasStock(@RequestParam("skuId") Long skuId,
                    @RequestParam("num") Integer num);
}
