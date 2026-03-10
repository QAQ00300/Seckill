package com.seckill.common.tools.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final ICustomError iCustomError;

    public CustomException(ICustomError iCustomError) {
        super(iCustomError.getMessage());
        this.iCustomError = iCustomError;
    }

    public CustomException(Integer code, String message) {
        super(message);
        this.iCustomError = ICustomError.of(code, message);
    }

    public CustomException(String message) {
        super(message);
        this.iCustomError = ICustomError.of(500, message);
    }

    public CustomException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.iCustomError = ICustomError.of(code, message);
    }
}