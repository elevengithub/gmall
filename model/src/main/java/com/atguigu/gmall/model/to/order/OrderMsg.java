package com.atguigu.gmall.model.to.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderMsg {
    private Long userId;
    private Long orderId;
}
