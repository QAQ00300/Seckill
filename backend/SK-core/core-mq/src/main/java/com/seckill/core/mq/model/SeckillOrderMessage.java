package com.seckill.core.mq.model;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 秒杀订单消息
 * 用于在阶段3通过MQ异步处理秒杀订单
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderMessage {

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 秒杀活动ID
     */
    private Long seckillId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 秒杀价格
     */
    private Double seckillPrice;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 秒杀开始时间
     */
    private LocalDateTime seckillStartTime;

    /**
     * 秒杀结束时间
     */
    private LocalDateTime seckillEndTime;

    /**
     * 消息创建时间
     */
    private LocalDateTime createTime;

    /**
     * 业务标识
     */
    private String businessKey;

    /**
     * 重试次数
     */
    private Integer retryCount;
}
