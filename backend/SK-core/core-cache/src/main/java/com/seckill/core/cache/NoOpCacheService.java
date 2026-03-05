package com.seckill.core.cache;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * 缓存空实现
 * 阶段1：不启用缓存，所有操作直接返回null或false
 */
@Service
@Primary  // 阶段1使用此实现
@Slf4j
public class NoOpCacheService implements CacheService {

    @Override
    public <T> void set(String key, T value, long expireSeconds) {
        log.debug("缓存未启用 - set: {}", key);
        // 阶段1不做任何操作
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        log.debug("缓存未启用 - get: {}", key);
        return null;  // 阶段1直接返回null，强制查询数据库
    }

    @Override
    public Boolean delete(String key) {
        log.debug("缓存未启用 - delete: {}", key);
        return false;
    }

    @Override
    public Boolean exists(String key) {
        return false;
    }

    @Override
    public Boolean expire(String key, long expireSeconds) {
        return false;
    }

    @Override
    public Long increment(String key, long delta) {
        log.debug("缓存未启用 - increment: {}", key);
        return null;  // 阶段1不支持原子操作
    }

    @Override
    public Long decrement(String key, long delta) {
        log.debug("缓存未启用 - decrement: {}", key);
        return null;  // 阶段1不支持原子操作
    }

    @Override
    public Boolean setIfAbsent(String key, String value, long expireSeconds) {
        log.debug("缓存未启用 - setIfAbsent: {}", key);
        return false;
    }

    @Override
    public Long getExpire(String key) {
        return -2L;  // Redis中-2表示key不存在
    }
}