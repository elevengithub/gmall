package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    SkuFeignClient skuFeignClient;
    @Autowired
    ThreadPoolExecutor executor;

    /**
     * 获取购物车中所有商品列表，并按照商品加入时间进行排序
     * @return
     */
    @Override
    public List<CartInfo> getCartList(String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        if (hashOps != null && hashOps.size() > 0) {
            //按照购物车中商品创建时间排序显示购物车列表
            List<CartInfo> cartInfos = hashOps.values()
                    .stream()
                    .map(s -> Jsons.toObj(s, CartInfo.class))
                    .sorted((o1,o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                    .collect(Collectors.toList());
            //获取商品的实时价格，更新购物车中商品的价格
            //由于使用线程池异步执行，feign远程调用使用线程池中的线程，导致丢失请求信息，透传的userId和userTempId
            //获取到老请求的请求信息
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            executor.submit(() -> {
                //绑定请求信息到异步执行的线程上
                RequestContextHolder.setRequestAttributes(attributes);
                updateCartAllItemsPrice(cartKey,cartInfos);
                //异步执行完任务，移除数据
                RequestContextHolder.resetRequestAttributes();
            });
            return cartInfos;
        }
        return null;
    }

    /**
     * 更新购物车中所有商品的价格为实时价格
     * @param cartKey
     * @param cartInfos
     */
    private void updateCartAllItemsPrice(String cartKey, List<CartInfo> cartInfos) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        for (CartInfo cartInfo : cartInfos) {
            Result<BigDecimal> result = skuFeignClient.getSkuPrice(cartInfo.getSkuId());
            cartInfo.setSkuPrice(result.getData());
            cartInfo.setUpdateTime(new Date());
            hashOps.put(cartInfo.getSkuId().toString(),Jsons.toStr(cartInfo));
        }
    }

    /**
     * 添加商品到购物车
     * @param skuId  商品id
     * @param skuNum  商品数量
     * @return
     */
    @Override
    public SkuInfo addCart(Long skuId,Integer skuNum){
        //获取用户的userid或者usertempid
        UserAuthInfo authInfo = AuthUtils.getCurrentAuthInfo();
        //1、根据用户的登录信息获取用户的购物车
        String cartKey = determinCartKey();
        //2、向购物车中添加商品
        SkuInfo skuInfo = addItem2Cart(skuId,skuNum,cartKey);
        //3、购物车超时设置，自动延期
        //不设置超时时间，默认ttl=-1，如果用户未登录一直使用临时id，则试着超时时间为90天
        if (authInfo.getUserId() == null) {
            String tempKey = SysRedisConst.CART_KRY + authInfo.getUserTempId();
            redisTemplate.expire(tempKey,90, TimeUnit.DAYS);
        }
        return skuInfo;
    }

    /**
     * 更新购物车中指定商品的数量+1或者-1
     * @param skuId 商品id
     * @param num +1 或 -1
     */
    @Override
    public void updateItemNum(Long skuId, Integer num) {
        String cartKey = determinCartKey();
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        CartInfo cartInfo = getItemFromCart(skuId, cartKey);
        cartInfo.setSkuNum(cartInfo.getSkuNum() + num);
        cartInfo.setUpdateTime(new Date());
        hashOps.put(skuId.toString(),Jsons.toStr(cartInfo));
    }

    /**
     * 更新购物车中商品的选中状态
     * @param skuId  商品id
     * @param isChecked  选中状态
     * @return
     */
    @Override
    public void updateChecked(Long skuId, Integer isChecked) {
        String cartKey = determinCartKey();
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        CartInfo cartInfo = getItemFromCart(skuId, cartKey);
        cartInfo.setIsChecked(isChecked);
        cartInfo.setUpdateTime(new Date());
        hashOps.put(skuId.toString(),Jsons.toStr(cartInfo));
    }

    /**
     * 根据商品id删除购物车中的该商品
     * @param skuId  商品id
     * @return
     */
    @Override
    public void deleteItem(Long skuId) {
        String cartKey = determinCartKey();
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        hashOps.delete(skuId.toString());
    }

    /**
     * 尝试合并临时id购物车到用户id购物车
     */
    @Override
    public void mergeUserAndTempCart() {
        UserAuthInfo authInfo = AuthUtils.getCurrentAuthInfo();
        //当临时id和用户id同时存在时，尝试合并购物车
        if (authInfo.getUserId() != null && !StringUtils.isEmpty(authInfo.getUserTempId())) {
            //创建临时购物车的购物车键
            String tempCartKey = SysRedisConst.CART_KRY + authInfo.getUserTempId();
            //获取临时id对应购物车中的所有商品
            List<CartInfo> tempCartList = getCartList(tempCartKey);
            if (tempCartList != null && tempCartList.size() > 0) {
                String cartKey = SysRedisConst.CART_KRY + authInfo.getUserId();
                for (CartInfo cartInfo : tempCartList) {
                    Long skuId = cartInfo.getSkuId();
                    Integer skuNum = cartInfo.getSkuNum();
                    addItem2Cart(skuId,skuNum,cartKey);
                    //保存完一个商品就清除掉临时购物车中的该商品
                    redisTemplate.opsForHash().delete(tempCartKey,skuId.toString());
                }
            }
        }
    }

    /**
     * 添加商品到购物车
     * @param skuId  商品id
     * @param skuNum  数量
     * @param cartKey  购物车
     * @return  商品信息
     */
    private SkuInfo addItem2Cart(Long skuId, Integer skuNum, String cartKey) {
        //1、查询商品信息
        SkuInfo skuInfo = skuFeignClient.getSkuInfo(skuId).getData();
        //2、判断用户之前是否添加过此商品，如果没有添加过，则新增
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        Boolean hasKey = hashOps.hasKey(skuId.toString());
        //2.1、新增之前获取购物车中商品的种类数量
        Long size = hashOps.size();
        if (!hasKey) {
            //如果大于200，则限制用户添加其他商品
            if (size + 1 > SysRedisConst.CART_ITEMS_LIMIT) {
                throw new GmallException(ResultCodeEnum.ADD_SKUTOCAT_OVERFLOW);
            }
            CartInfo cartInfo = converseSkuInfo2CartInfo(skuInfo);
            cartInfo.setSkuNum(skuNum);
            hashOps.put(skuId.toString(), Jsons.toStr(cartInfo));
        } else {
            //3、用户之前添加过该商品，修改购物车中该商品的数量
            CartInfo cartInfo = getItemFromCart(skuId,cartKey);
            cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);
            hashOps.put(skuId.toString(),Jsons.toStr(cartInfo));
        }
        return skuInfo;
    }

    /**
     * 从购物车中获取CartInfo
     * @param skuId
     * @param cartKey
     * @return
     */
    private CartInfo getItemFromCart(Long skuId, String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        String cartInfoStr = hashOps.get(skuId.toString());
        CartInfo cartInfo = Jsons.toObj(cartInfoStr,CartInfo.class);
        return cartInfo;
    }

    /**
     * 将skuInfo转换为cartInfo
     * @param skuInfo
     * @return
     */
    private CartInfo converseSkuInfo2CartInfo(SkuInfo skuInfo) {
        CartInfo cartInfo = new CartInfo();
        cartInfo.setSkuId(skuInfo.getId());
        cartInfo.setCartPrice(skuInfo.getPrice());
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfo.getSkuName());
        cartInfo.setIsChecked(1);
        cartInfo.setCreateTime(new Date());
        cartInfo.setUpdateTime(new Date());
        cartInfo.setSkuPrice(skuInfo.getPrice());
        return cartInfo;
    }

    /**
     * 根据用户的登录信息决定用哪个购物车键
     * @return
     */
    public String determinCartKey() {
        UserAuthInfo authInfo = AuthUtils.getCurrentAuthInfo();
        String cartKey = SysRedisConst.CART_KRY;
        if(authInfo.getUserId() != null){
            //用户登录了，就使用userid
            cartKey = cartKey + authInfo.getUserId();
        } else {
            //用户未登录，使用usertempid
            cartKey = cartKey + authInfo.getUserTempId();
        }
        return cartKey;
    }

    /**
     * 删除购物车中选中的商品
     * @return
     */
    @Override
    public void deleteChecked(String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

        //1、拿到选中的商品，并删除。收集所有选中商品的id
        List<String> ids = getCheckedItems(cartKey).stream()
                .map(cartInfo -> cartInfo.getSkuId().toString())
                .collect(Collectors.toList());

        if(ids!=null && ids.size() > 0){
            hashOps.delete(ids.toArray());
        }


    }

    /**
     * 拿到购物车中所有选中的商品
     * @param cartKey
     * @return
     */
    public List<CartInfo> getCheckedItems(String cartKey) {
        List<CartInfo> cartList = getCartList(cartKey);
        List<CartInfo> checkedItems = cartList.stream()
                .filter(cartInfo -> cartInfo.getIsChecked() == 1)
                .collect(Collectors.toList());
        return checkedItems;
    }
}
