package com.seckill.core.cache;


/**
 * 缓存服务接口
 * 阶段1：定义接口，为阶段2的Redis实现做准备
 */
public interface CacheService {

    /**
     * 设置缓存
     */
    <T> void set(String key, T value, long expireSeconds);

    /**
     * 获取缓存
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 删除缓存
     */
    Boolean delete(String key);

    /**
     * 判断key是否存在
     */
    Boolean exists(String key);

    /**
     * 设置过期时间
     */
    Boolean expire(String key, long expireSeconds);

    /**
     * 原子递增
     */
    Long increment(String key, long delta);

    /**
     * 原子递减
     */
    Long decrement(String key, long delta);

    /**
     * 设置值（如果不存在）
     */
    Boolean setIfAbsent(String key, String value, long expireSeconds);

    /**
     * 获取剩余过期时间
     */
    Long getExpire(String key);
}