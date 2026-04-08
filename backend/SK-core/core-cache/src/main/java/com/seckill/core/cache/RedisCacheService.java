package com.seckill.core.cache;


import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存实现
 */
@Service
@Primary
@Slf4j
public class RedisCacheService implements CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public <T> void set(String key, T value, long expireSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
            log.debug("Redis set: {} = {}, expire: {}s", key, value, expireSeconds);
        } catch (Exception e) {
            log.error("Redis set error: key={}, value={}", key, value, e);
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                T result = (T) value;
                log.debug("Redis get: {} = {}", key, result);
                return result;
            }
        } catch (Exception e) {
            log.error("Redis get error: key={}", key, e);
        }
        return null;
    }

    @Override
    public Boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            log.debug("Redis delete: {} = {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("Redis delete error: key={}", key, e);
            return false;
        }
    }

    @Override
    public Boolean exists(String key) {
        try {
            Boolean result = redisTemplate.hasKey(key);
            log.debug("Redis exists: {} = {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("Redis exists error: key={}", key, e);
            return false;
        }
    }

    @Override
    public Boolean expire(String key, long expireSeconds) {
        try {
            Boolean result = redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
            log.debug("Redis expire: {} = {}, seconds: {}", key, result, expireSeconds);
            return result;
        } catch (Exception e) {
            log.error("Redis expire error: key={}", key, e);
            return false;
        }
    }

    @Override
    public Long increment(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().increment(key, delta);
            log.debug("Redis increment: {} = {}, delta: {}", key, result, delta);
            return result;
        } catch (Exception e) {
            log.error("Redis increment error: key={}", key, e);
            return null;
        }
    }

    @Override
    public Long decrement(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().decrement(key, delta);
            log.debug("Redis decrement: {} = {}, delta: {}", key, result, delta);
            return result;
        } catch (Exception e) {
            log.error("Redis decrement error: key={}", key, e);
            return null;
        }
    }

    @Override
    public Boolean setIfAbsent(String key, String value, long expireSeconds) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, expireSeconds, TimeUnit.SECONDS);
            log.debug("Redis setIfAbsent: {} = {}, value: {}, expire: {}s", key, result, value, expireSeconds);
            return result;
        } catch (Exception e) {
            log.error("Redis setIfAbsent error: key={}", key, e);
            return false;
        }
    }

    @Override
    public Long getExpire(String key) {
        try {
            Long result = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            log.debug("Redis getExpire: {} = {}s", key, result);
            return result;
        } catch (Exception e) {
            log.error("Redis getExpire error: key={}", key, e);
            return -2L; // Redis中-2表示key不存在
        }
    }
}