package com.seckill.core.boot.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
;

/**
 * 秒杀业务异常类
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillException extends RuntimeException {
    private Integer code;

    public SeckillException(Integer code) {
        this.code = code;
    }

}
