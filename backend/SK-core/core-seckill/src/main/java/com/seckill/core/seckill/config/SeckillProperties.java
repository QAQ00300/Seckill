package com.seckill.core.seckill.config;



import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 秒杀配置属性
 */
@Data
@ConfigurationProperties(prefix = "sk.seckill")
public class SeckillProperties {

    /**
     * 是否启用秒杀服务
     */
    private boolean enabled = true;

    /**
     * 秒杀处理策略
     * sync：同步处理（阶段1）
     * async：异步处理（阶段3）
     */
    private String strategy = "sync";

    /**
     * 最大购买数量
     */
    private Integer maxQuantity = 10;

    /**
     * 用户购买频率限制（秒）
     */
    private Integer userRateLimit = 60;

    /**
     * IP购买频率限制（秒）
     */
    private Integer ipRateLimit = 30;

    /**
     * 是否开启风控
     */
    private boolean riskControlEnabled = false;

    /**
     * 是否验证用户资格
     */
    private boolean validateUserEnabled = true;

    /**
     * 是否验证库存
     */
    private boolean validateStockEnabled = true;

    /**
     * 超时时间（毫秒）
     */
    private Integer timeout = 5000;

    /**
     * 重试次数
     */
    private Integer retryCount = 0;
}