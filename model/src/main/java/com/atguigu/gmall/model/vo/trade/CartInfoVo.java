package com.atguigu.gmall.model.vo.trade;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartInfoVo {
    private String imgUrl;
    private String skuName;
    private BigDecimal orderPrice;
    private Integer skuNum;
    private Long skuId;
    private String hasStock;
}
