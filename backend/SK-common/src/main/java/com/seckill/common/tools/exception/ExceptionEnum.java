package com.seckill.common.tools.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionEnum implements ICustomError{
    ;

    private final Integer code;
    private final String message;

    public static ExceptionEnum valueOf(Integer code) {
        for (ExceptionEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    public void throwsException() {
        throw new CustomException(ExceptionEnum.this);
    }

}
