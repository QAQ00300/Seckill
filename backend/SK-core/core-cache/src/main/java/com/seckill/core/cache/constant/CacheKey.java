package com.seckill.core.cache.constant;


/**
 * 缓存Key常量定义
 */
public class CacheKey {

    // 用户相关
    public static final String USER_PREFIX = "user:";
    public static final String USER_ID_KEY = "user:id:";

    // 商品相关
    public static final String PRODUCT_PREFIX = "product:";
    public static final String PRODUCT_ID_KEY = "product:id:";
    public static final String PRODUCT_LIST_KEY = "product:list:";

    // 秒杀相关
    public static final String SECKILL_PREFIX = "seckill:";
    public static final String SECKILL_ID_KEY = "seckill:id:";
    public static final String SECKILL_LIST_KEY = "seckill:list:";
    public static final String SECKILL_STOCK_KEY = "seckill:stock:";

    // 库存相关
    public static final String STOCK_PREFIX = "stock:";
    public static final String STOCK_DEDUCT_KEY = "stock:deduct:";

    // 订单相关
    public static final String ORDER_PREFIX = "order:";
    public static final String ORDER_ID_KEY = "order:id:";
    public static final String ORDER_NO_KEY = "order:no:";

    // 分布式锁
    public static final String LOCK_PREFIX = "lock:";
    public static final String SECKILL_LOCK_KEY = "lock:seckill:";
    public static final String STOCK_LOCK_KEY = "lock:stock:";

    /**
     * 构建用户缓存Key
     */
    public static String buildUserKey(Long userId) {
        return USER_ID_KEY + userId;
    }

    /**
     * 构建商品缓存Key
     */
    public static String buildProductKey(Long productId) {
        return PRODUCT_ID_KEY + productId;
    }

    /**
     * 构建秒杀活动缓存Key
     */
    public static String buildSeckillKey(Long seckillId) {
        return SECKILL_ID_KEY + seckillId;
    }

    /**
     * 构建秒杀库存缓存Key
     */
    public static String buildSeckillStockKey(Long seckillId) {
        return SECKILL_STOCK_KEY + seckillId;
    }

    /**
     * 构建分布式锁Key
     */
    public static String buildSeckillLockKey(Long seckillId) {
        return SECKILL_LOCK_KEY + seckillId;
    }
}