package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JMeterTestController {

    @GetMapping("/jmeter/test")
    public Result JMeterTest(){
        return Result.ok();
    }
}
