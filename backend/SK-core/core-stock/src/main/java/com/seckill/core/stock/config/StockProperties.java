package com.seckill.core.stock.config;



import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 库存配置属性
 */
@Data
@ConfigurationProperties(prefix = "sk.stock")
public class StockProperties {

    /**
     * 库存扣减策略
     * db：数据库扣减（阶段1）
     * redis：Redis预扣减（阶段2）
     */
    private String deductStrategy = "db";

    /**
     * 是否启用库存预热
     */
    private boolean preheatEnabled = false;

    /**
     * 库存预热提前时间（分钟）
     */
    private Integer preheatAheadMinutes = 5;

    /**
     * 库存同步频率（秒）
     */
    private Integer syncFrequency = 60;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount = 3;

    /**
     * 重试间隔（毫秒）
     */
    private Integer retryInterval = 1000;

    /**
     * 库存告警阈值
     */
    private Integer alarmThreshold = 10;

    /**
     * 是否开启库存告警
     */
    private boolean alarmEnabled = false;
}