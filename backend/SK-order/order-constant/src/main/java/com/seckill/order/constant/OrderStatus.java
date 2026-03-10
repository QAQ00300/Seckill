package com.seckill.order.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING(0, "待处理", Arrays.asList(PAID, CANCELLED)),
    PAID(1, "已支付", Arrays.asList(COMPLETED, REFUNDED)),
    COMPLETED(2, "已完成", Arrays.asList()),
    CANCELLED(3, "已取消", Arrays.asList()),
    REFUNDED(4, "已退款", Arrays.asList());

    private final Integer code;
    private final String description;
    private final List<OrderStatus> nextStatuses;

    /**
     * 检查状态转换是否合法
     */
    public boolean canTransferTo(OrderStatus targetStatus) {
        if (targetStatus == null) {
            return false;
        }
        return this.nextStatuses.contains(targetStatus);
    }

    public static OrderStatus fromCode(Integer code) {
        for (OrderStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown order status: " + code);
    }
}