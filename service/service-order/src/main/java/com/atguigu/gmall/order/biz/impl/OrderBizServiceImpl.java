package com.atguigu.gmall.order.biz.impl;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.trade.CartInfoVo;
import com.atguigu.gmall.model.vo.trade.OrderConfirmDataVo;
import com.atguigu.gmall.model.vo.trade.OrderSubmitVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderBizServiceImpl implements OrderBizService {

    @Autowired
    CartFeignClient cartFeignClient;
    @Autowired
    UserFeignClient userFeignClient;
    @Autowired
    SkuFeignClient skuFeignClient;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    WareFeignClient wareFeignClient;
    @Autowired
    OrderInfoService orderInfoService;

    /**
     * 获取订单确认页需要的数据
     * @return
     */
    @Override
    public OrderConfirmDataVo getOrderConfirmData() {
        OrderConfirmDataVo vo = new OrderConfirmDataVo();
        //1、获取订单中所有商品详情的集合
        List<CartInfo> checkedList = cartFeignClient.getCheckedList().getData();
        List<CartInfoVo> cartInfoVos = checkedList.stream()
                .map(cartInfo -> {
                    CartInfoVo cartInfoVo = new CartInfoVo();
                    cartInfoVo.setSkuId(cartInfo.getSkuId());
                    cartInfoVo.setImgUrl(cartInfo.getImgUrl());
                    cartInfoVo.setSkuName(cartInfo.getSkuName());
                    //只要使用到商品价格就要区数据库查询实时价格
                    Result<BigDecimal> result = skuFeignClient.getSkuPrice(cartInfo.getSkuId());
                    cartInfoVo.setOrderPrice(result.getData());
                    cartInfoVo.setSkuNum(cartInfo.getSkuNum());
                    return cartInfoVo;
                }).collect(Collectors.toList());
        vo.setDetailArrayList(cartInfoVos);
        //2、获取订单中商品的总数量
        Integer totalNum = checkedList.stream()
                .map(cartInfo -> cartInfo.getSkuNum())
                .reduce((o1, o2) -> o1 + o2).get();
        vo.setTotalNum(totalNum);
        //3、获取订单的总价钱
        BigDecimal totalAmount = cartInfoVos.stream()
                .map(cartInfoVo -> cartInfoVo.getOrderPrice().multiply(new BigDecimal(cartInfoVo.getSkuNum() + "")))
                .reduce((o1, o2) -> o1.add(o2)).get();
        vo.setTotalAmount(totalAmount);
        //4、获取用户的收货地址集合
        Result<List<UserAddress>> userAddressList = userFeignClient.getUserAddressList();
        vo.setUserAddressList(userAddressList.getData());
        //5、设置交易追踪号
        //作用一：订单的唯一追踪号，对外交易号（和第三方进行交互使用）
        //作用二：防止重复提交
        String tradeNo = generateTradeNo();
        vo.setTradeNo(tradeNo);
        return vo;
    }

    /**
     * 保存订单信息到数据库
     * @param tradeNo 订单唯一追踪号
     * @param vo 订单信息vo类
     * @return 订单id
     */
    @Override
    public Long submitOrder(String tradeNo, OrderSubmitVo vo) {
        //1、校验订单追踪号，防止订单重复提交
        boolean checked = checkTradeNo(tradeNo);
        if (!checked) {
            throw new GmallException(ResultCodeEnum.TOKEN_INVAILD);
        }
        //2、校验库存
        List<String> noStockSkus = new ArrayList<>();
        for (CartInfoVo cartInfoVo : vo.getOrderDetailList()) {
            String stock = wareFeignClient.hasStock(cartInfoVo.getSkuId(),
                    cartInfoVo.getSkuNum());
            if (!"1".equals(stock)) {
                noStockSkus.add(stock);
            }
        }
        if (noStockSkus.size() > 0) {
            String stockSkuNames = noStockSkus.stream()
                    .reduce((s1, s2) -> s1 + " " + s2).get();
            throw new GmallException(ResultCodeEnum.ORDER_NO_STOCK.getMessage() + stockSkuNames,
                    ResultCodeEnum.ORDER_NO_STOCK.getCode());
        }
        //3、校验价格
        List<String> skuNames = new ArrayList<>();
        for (CartInfoVo cartInfoVo : vo.getOrderDetailList()) {
            BigDecimal skuPrice = skuFeignClient.getSkuPrice(cartInfoVo.getSkuId()).getData();
            if (!skuPrice.equals(cartInfoVo.getOrderPrice())) {
                skuNames.add(cartInfoVo.getSkuName());
            }
        }
        if (skuNames.size() > 0) {
            String result = skuNames.stream()
                    .reduce((s1, s2) -> s1 + " " + s2).get();
            throw new GmallException(ResultCodeEnum.ORDER_PRICE_CHANGED.getMessage() + result,
                    ResultCodeEnum.ORDER_PRICE_CHANGED.getCode());
        }
        //4、订单信息保存至数据库，保存成功后发消息到延迟队列
        Long orderId = orderInfoService.saveOrder(tradeNo,vo);

        //5、清除购物车中选中的商品
        cartFeignClient.deleteChecked();
        return orderId;
    }

    /**
     * 关闭订单
     * @param orderId  订单id
     * @param userId  用户id
     */
    @Override
    public void closeOrder(Long orderId, Long userId) {
        //如果订单逾期未支付或者在45分钟之内已经完成支付，则不进行关单操作
        //1、获取订单未支付和已完成的状态
        List<ProcessStatus> expected = Arrays.asList(ProcessStatus.UNPAID,ProcessStatus.FINISHED);
        //2、调用mapper层完成关闭订单操作
        orderInfoService.changeOrderStatus(orderId,userId,ProcessStatus.CLOSED,expected);
    }

    /**
     * 校验订单追踪号
     * @param tradeNo 订单追踪号
     * @return 是否正确
     */
    private boolean checkTradeNo(String tradeNo) {
//        //判断redis中是否存在前端传过来的订单追踪号
//        String s = redisTemplate.opsForValue().get(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo);
//        if (!StringUtils.isEmpty(s)) {
//            //证明订单追踪号正确，返回true，并删除redis中的订单追踪号
//            redisTemplate.delete(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo);
//            return true;
//        }
//        return false;
        //使用lua脚本执行验证并删除订单追踪号，保证原子性。
        String lua = "if redis.call('get',KEYS[1]) == ARGV[1] then " +
                "return redis.call('del',KEYS[1]) " +
                "else " +
                "return 0 " +
                "end";
        Long execute = redisTemplate.execute(new DefaultRedisScript<Long>(lua, Long.class),
                Arrays.asList(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo),
                new String[]{"1"});
        if (execute > 0) {
            return true;
        }
        return false;
    }

    /**
     * 生成交易追踪号
     * @return
     */
    private String generateTradeNo() {
        //同一个用户同一毫秒只能下一单
        long timeMillis = System.currentTimeMillis();
        Long userId = AuthUtils.getCurrentAuthInfo().getUserId();
        String tradeNo = timeMillis + "_" + userId;
        //redis中存一份交易追踪号进行后续校验使用
        redisTemplate.opsForValue().set(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo,
                "1",15, TimeUnit.MINUTES);
        return tradeNo;
    }
}
