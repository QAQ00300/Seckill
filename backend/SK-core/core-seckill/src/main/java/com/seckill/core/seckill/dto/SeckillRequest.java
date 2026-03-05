package com.seckill.core.seckill.dto;



import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 秒杀请求DTO
 */
@Data
public class SeckillRequest {

    /**
     * 请求ID（用于幂等）
     */
    private String requestId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 秒杀活动ID
     */
    @NotNull(message = "秒杀活动ID不能为空")
    private Long seckillId;

    /**
     * 购买数量
     */
    @Min(value = 1, message = "购买数量至少为1")
    private Integer quantity = 1;

    /**
     * 用户IP（用于风控）
     */
    private String userIp;

    /**
     * 用户设备指纹
     */
    private String deviceFingerprint;

    /**
     * 请求时间戳
     */
    private Long timestamp = System.currentTimeMillis();
}