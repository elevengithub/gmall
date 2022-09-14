package com.atguigu.gmall.model.vo.trade;

import com.atguigu.gmall.model.user.UserAddress;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderConfirmDataVo {
    private List<CartInfoVo> detailArrayList;
    //下单的商品总数量
    private Integer totalNum;
    private BigDecimal totalAmount;
    private List<UserAddress> userAddressList;
    //交易追踪号
    private String tradeNo;
}
