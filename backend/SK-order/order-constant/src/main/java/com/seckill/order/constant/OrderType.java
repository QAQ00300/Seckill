package com.seckill.order.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderType {

    NORMAL(0, "普通订单"),
    SECKILL(1, "秒杀订单");

    private final Integer code;
    private final String description;

    public static OrderType fromCode(Integer code) {
        for (OrderType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown order type: " + code);
    }
}