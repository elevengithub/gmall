package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;

    //http://cart.gmall.com/addCart.html?skuId=49&skuNum=1&sourceType=query
    /**
     * 添加商品到购物车
     * @param skuId  商品id
     * @param skuNum  商品数量
     * @return
     */
    @GetMapping("addCart.html")
    public String addCart(@RequestParam("skuId") Long skuId,
                          @RequestParam("skuNum") Integer skuNum,
                          Model model){
        Result<SkuInfo> result = cartFeignClient.addCart(skuId,skuNum);
        if (result.isOk()) {
            model.addAttribute("skuInfo",result.getData());
            model.addAttribute("skuNum",skuNum);
            return "cart/addCart";
        } else {
            model.addAttribute("msg",result.getData());
            return "cart/error";
        }
    }

    //http://cart.gmall.com/cart.html
    /**
     * 购物车展示页
     * @return
     */
    @GetMapping("cart.html")
    public String cartHtml(){
        return "cart/index";
    }

    //http://cart.gmall.com/cart/deleteChecked

    /**
     * 删除选中的购物项
     * @return
     */
    @GetMapping("/cart/deleteChecked")
    public String deleteChecked(){
        cartFeignClient.deleteChecked();
        return "redirect:http://cart.gmall.com/cart.html";
    }
}
