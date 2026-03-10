package com.seckill.user.constant;

import com.seckill.common.tools.exception.ICustomError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ICustomError {

    USER_NOT_FOUND(1001, "用户不存在"),
    USERNAME_DUPLICATE(1002, "用户名已存在"),
    PHONE_DUPLICATE(1003, "手机号已被注册"),
    EMAIL_DUPLICATE(1004, "邮箱已被注册"),
    USER_DISABLED(1005, "用户已被禁用"),
    INVALID_PARAMETER(1006, "参数无效");

    private final Integer code;
    private final String message;
}