package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SkuBitMapController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("/sync/skuid/bitmap")
    public Result setBitMap(){
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        return Result.ok();
    }
}
