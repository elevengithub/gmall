package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;

import java.util.List;

public interface CartService {
    /**
     * 获取购物车中所有商品列表
     * @return
     */
    List<CartInfo> getCartList(String cartKey);

    /**
     * 添加商品到购物车
     * @param skuId
     * @param skuNum
     * @return
     */
    SkuInfo addCart(Long skuId, Integer skuNum);

    /**
     * 更新购物车中指定商品的数量+1或者-1
     * @param skuId 商品id
     * @param num +1 或 -1
     */
    void updateItemNum(Long skuId, Integer num);

    /**
     * 更新购物车中商品的选中状态
     * @param skuId  商品id
     * @param isChecked  选中状态
     * @return
     */
    void updateChecked(Long skuId, Integer isChecked);

    /**
     * 根据商品id删除购物车中的该商品
     * @param skuId  商品id
     * @return
     */
    void deleteItem(Long skuId);

    /**
     * 尝试合并临时id购物车到用户id购物车
     */
    void mergeUserAndTempCart();

    /**
     * 根据用户的登录信息决定用哪个购物车键
     * @return
     */
    String determinCartKey();

    /**
     * 删除购物车中选中的商品
     * @return
     */
    void deleteChecked(String cartKey);

    /**
     * 拿到购物车中所有选中的商品
     * @param cartKey
     * @return
     */
    List<CartInfo> getCheckedItems(String cartKey);
}
