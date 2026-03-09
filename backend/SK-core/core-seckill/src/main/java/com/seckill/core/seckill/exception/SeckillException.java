package com.seckill.core.seckill.exception;

import lombok.Getter;

/**
 * 秒杀业务异常类
 */
@Getter
public class SeckillException extends RuntimeException {

    private final Integer code;

    public SeckillException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public SeckillException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public SeckillException(String message) {
        super(message);
        this.code = 500;
    }
}