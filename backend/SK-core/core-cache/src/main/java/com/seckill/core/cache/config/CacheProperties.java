package com.seckill.core.cache.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 缓存配置属性
 */
@Data
@ConfigurationProperties(prefix = "seckill.cache")
public class CacheProperties {

    /**
     * 是否启用缓存
     * 阶段1：false（不启用）
     * 阶段2：true（启用Redis）
     */
    private boolean enabled = false;

    /**
     * 缓存类型
     * none：无缓存（阶段1）
     * redis：Redis缓存（阶段2）
     */
    private String type = "none";

    /**
     * 默认过期时间（秒）
     */
    private long defaultExpire = 300;  // 5分钟

    /**
     * 用户信息过期时间
     */
    private long userExpire = 3600;  // 1小时

    /**
     * 商品信息过期时间
     */
    private long productExpire = 600;  // 10分钟

    /**
     * 秒杀活动过期时间
     */
    private long seckillExpire = 300;  // 5分钟

    /**
     * 库存缓存过期时间
     */
    private long stockExpire = 7200;  // 2小时

    /**
     * 分布式锁过期时间
     */
    private long lockExpire = 30;  // 30秒
}
