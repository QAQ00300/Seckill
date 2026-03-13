package com.seckill.order.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING(0, "待处理"),
    PAID(1, "已支付"),
    COMPLETED(2, "已完成"),
    CANCELLED(3, "已取消"),
    REFUNDED(4, "已退款");

    private final int code;
    private final String desc;

    // 获取下一个可能的状态
    public List<OrderStatus> getNextStatuses() {
        return switch (this) {
            case PENDING -> Arrays.asList(PAID, CANCELLED);
            case PAID -> Arrays.asList(COMPLETED, REFUNDED);
            case COMPLETED -> Arrays.asList();
            case CANCELLED -> Arrays.asList();
            case REFUNDED -> Arrays.asList();
        };
    }
}