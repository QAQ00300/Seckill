package com.seckill.core.seckill.validator;


import com.seckill.core.seckill.dto.SeckillRequest;
import com.seckill.core.boot.exception.SeckillException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 秒杀请求验证器
 */
@Component
@Slf4j
public class SeckillValidator {

    /**
     * 验证秒杀请求
     */
    public void validateRequest(SeckillRequest request) {
        if (request == null) {
            throw new SeckillException(101);
        }

        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new SeckillException(102);
        }

        if (request.getSeckillId() == null || request.getSeckillId() <= 0) {
            throw new SeckillException(103);
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new SeckillException(104);
        }

        // 验证请求时间（防止重放攻击）
        validateTimestamp(request.getTimestamp());

        // 基础风控验证
        validateRiskControl(request);
    }

    /**
     * 验证时间戳
     */
    private void validateTimestamp(Long timestamp) {
        if (timestamp == null) {
            throw new SeckillException(105);
        }

        long currentTime = System.currentTimeMillis();
        long diff = Math.abs(currentTime - timestamp);

        // 允许5分钟的时间偏差
        if (diff > 5 * 60 * 1000) {
            throw new SeckillException(106);
        }
    }

    /**
     * 基础风控验证
     */
    private void validateRiskControl(SeckillRequest request) {
        // 阶段1：简单验证，后续可接入风控系统
        log.debug("风控验证: userId={}, ip={}", request.getUserId(), request.getUserIp());

        // 可添加：IP黑名单、用户购买频率限制等
    }
}
