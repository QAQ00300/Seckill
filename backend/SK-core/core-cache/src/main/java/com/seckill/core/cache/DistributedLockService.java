package com.seckill.core.cache;

/**
 * 分布式锁服务接口
 */
public interface DistributedLockService {

    /**
     * 尝试获取分布式锁
     * @param lockKey 锁的key
     * @param expireSeconds 锁的过期时间（秒）
     * @return 是否获取成功
     */
    boolean tryLock(String lockKey, long expireSeconds);

    /**
     * 释放分布式锁
     * @param lockKey 锁的key
     * @return 是否释放成功
     */
    boolean unlock(String lockKey);

    /**
     * 尝试获取分布式锁，带重试机制
     * @param lockKey 锁的key
     * @param expireSeconds 锁的过期时间（秒）
     * @param retryCount 重试次数
     * @param retryIntervalMs 重试间隔（毫秒）
     * @return 是否获取成功
     */
    boolean tryLockWithRetry(String lockKey, long expireSeconds, int retryCount, long retryIntervalMs);
}