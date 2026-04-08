package com.seckill.core.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁实现
 */
@Service
@Slf4j
public class RedisDistributedLockService implements DistributedLockService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean tryLock(String lockKey, long expireSeconds) {
        try {
            // 使用setIfAbsent命令实现分布式锁
            Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", expireSeconds, TimeUnit.SECONDS);
            log.debug("尝试获取分布式锁: key={}, result={}", lockKey, result);
            return result != null && result;
        } catch (Exception e) {
            log.error("获取分布式锁异常: key={}", lockKey, e);
            return false;
        }
    }

    @Override
    public boolean unlock(String lockKey) {
        try {
            Boolean result = redisTemplate.delete(lockKey);
            log.debug("释放分布式锁: key={}, result={}", lockKey, result);
            return result != null && result;
        } catch (Exception e) {
            log.error("释放分布式锁异常: key={}", lockKey, e);
            return false;
        }
    }

    @Override
    public boolean tryLockWithRetry(String lockKey, long expireSeconds, int retryCount, long retryIntervalMs) {
        try {
            for (int i = 0; i <= retryCount; i++) {
                boolean acquired = tryLock(lockKey, expireSeconds);
                if (acquired) {
                    return true;
                }
                if (i < retryCount) {
                    log.debug("获取分布式锁失败，{}ms后重试: key={}", retryIntervalMs, lockKey);
                    Thread.sleep(retryIntervalMs);
                }
            }
        } catch (InterruptedException e) {
            log.error("获取分布式锁重试被中断: key={}", lockKey, e);
            Thread.currentThread().interrupt();
        }
        return false;
    }
}