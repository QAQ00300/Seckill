package com.seckill.common.tools.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface ICustomError {

    @Getter
    @AllArgsConstructor
    class SimpleCustomError implements ICustomError {
        private Integer code;
        private String message;
    }

    static ICustomError of(Integer code, String message) {
        return new SimpleCustomError(code, message);
    }

    Integer getCode();
    String getMessage();
}
