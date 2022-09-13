package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/cart")
@RestController
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * 获取购物车中所有商品集合
     * @return
     */
    @GetMapping("/cartList")
    public Result cartList(){
        String cartKey = cartService.determinCartKey();
        //尝试将临时id的购物车合并到登录后用户的购物车
        cartService.mergeUserAndTempCart();
        List<CartInfo> cartInfos = cartService.getCartList(cartKey);
        return Result.ok(cartInfos);
    }

    /**
     * 更新购物车中指定商品的数量+1或者-1
     * @param skuId 商品id
     * @param num +1 或 -1
     */
    @PostMapping("/addToCart/{skuId}/{num}")
    public Result updateItemNum(@PathVariable("skuId") Long skuId,
                                @PathVariable("num") Integer num){
        cartService.updateItemNum(skuId,num);
        return Result.ok();
    }

    /**
     * 更新购物车中商品的选中状态
     * @param skuId  商品id
     * @param isChecked  选中状态
     * @return
     */
    @GetMapping("/checkCart/{skuId}/{isChecked}")
    public Result updateChecked(@PathVariable("skuId") Long skuId,
                                @PathVariable("isChecked") Integer isChecked){
        cartService.updateChecked(skuId,isChecked);
        return Result.ok();
    }

    /**
     * 根据商品id删除购物车中的该商品
     * @param skuId  商品id
     * @return
     */
    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteItem(@PathVariable("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return Result.ok();
    }
}
