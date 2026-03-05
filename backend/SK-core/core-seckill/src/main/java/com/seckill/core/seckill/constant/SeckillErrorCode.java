package com.seckill.core.seckill.constant;

import lombok.Getter;

@Getter
public enum SeckillErrorCode {

    REQUEST_PARAM_NULL(101, "请求参数不能为空"),
    USER_ID_INVALID(102, "用户ID无效"),
    SECKILL_ID_INVALID(103, "秒杀活动ID无效"),
    QUANTITY_INVALID(104, "购买数量无效"),
    TIMESTAMP_NULL(105, "时间戳不能为空"),
    REQUEST_EXPIRED(106, "请求已过期");


    private Integer code;
    private String message;

    SeckillErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
