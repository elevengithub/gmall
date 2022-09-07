package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.common.config.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/inner/rpc/cart")
@RestController
public class CartApiController {

    /**
     * 添加商品到购物车
     * @param skuId  商品id
     * @param skuNum  商品数量
     * @return
     */
    @GetMapping("/addCart")
    public Result addCart(@RequestParam("skuId") Long skuId,
                          @RequestParam("skuNum") Integer skuNum,
                          @RequestHeader(value = SysRedisConst.USERID_HEADER,required = false) String userId){
        System.out.println("用户id为：" + userId);
        return Result.ok();
    }
}
