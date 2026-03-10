package com.seckill.order.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderErrorCode {

    // 通用错误 1000-1999
    ORDER_NOT_FOUND(1001, "订单不存在"),
    ORDER_CREATE_FAILED(1002, "订单创建失败"),
    ORDER_UPDATE_FAILED(1003, "订单更新失败"),
    ORDER_DELETE_FAILED(1004, "订单删除失败"),

    // 订单状态错误 2000-2999
    ORDER_STATUS_INVALID(2001, "订单状态无效"),
    ORDER_CANCEL_NOT_ALLOWED(2002, "订单不允许取消"),

    // 参数验证错误 3000-3999
    USER_ID_INVALID(3001, "用户 ID 无效"),
    PRODUCT_ID_INVALID(3002, "商品 ID 无效"),
    QUANTITY_INVALID(3003, "购买数量无效"),
    PRICE_INVALID(3004, "订单金额无效"),

    // 秒杀订单特有错误 4000-4999
    SECKILL_ORDER_CREATE_FAILED(4001, "秒杀订单创建失败"),
    SECKILL_ALREADY_PARTICIPATED(4002, "已参加过该秒杀活动"),
    SECKILL_ACTIVITY_NOT_FOUND(4003, "秒杀活动不存在");

    private final Integer code;
    private final String message;
}