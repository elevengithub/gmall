package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/inner/rpc/cart")
@RestController
public class CartApiController {

    @Autowired
    CartService cartService;


    /**
     * 添加商品到购物车
     * @param skuId  商品id
     * @param skuNum  商品数量
     * @return
     */
    @GetMapping("/addCart")
    public Result<SkuInfo> addCart(@RequestParam("skuId") Long skuId,
                                   @RequestParam("skuNum") Integer skuNum){
        SkuInfo skuInfo = cartService.addCart(skuId,skuNum);
        return Result.ok(skuInfo);
    }

    /**
     * 删除购物车中选中的商品
     * @return
     */
    @GetMapping("/deleteChecked")
    public Result deleteChecked(){
        String cartKey = cartService.determinCartKey();
        cartService.deleteChecked(cartKey);
        return Result.ok();
    }
}
