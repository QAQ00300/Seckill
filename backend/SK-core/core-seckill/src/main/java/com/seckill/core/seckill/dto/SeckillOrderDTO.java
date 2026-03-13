package com.seckill.core.seckill.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 秒杀订单 DTO（用于 Feign 调用）
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeckillOrderDTO {
    private Long userId;
    private Long seckillId;
    private Long productId;
    private BigDecimal unitPrice;
    private BigDecimal seckillPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Integer orderStatus;
}