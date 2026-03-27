package com.seckill.core.cache.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seckill.core.cache.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisCacheService implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> void set(String key, T value, long expireSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
            log.debug("设置缓存 - key: {}, expire: {}s", key, expireSeconds);
        } catch (Exception e) {
            log.error("设置缓存失败 - key: {}", key, e);
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.debug("缓存未命中 - key: {}", key);
                return null;
            }

            // 如果是字符串直接返回，否则转换为 JSON
            if (clazz == String.class) {
                return clazz.cast(value);
            }

            // 复杂对象通过 JSON 反序列化
            String json = objectMapper.writeValueAsString(value);
            T result = objectMapper.readValue(json, clazz);
            log.debug("缓存命中 - key: {}, type: {}", key, clazz.getSimpleName());
            return result;
        } catch (Exception e) {
            log.error("获取缓存失败 - key: {}", key, e);
            return null;
        }
    }

    @Override
    public Boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            log.debug("删除缓存 - key: {}, success: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("删除缓存失败 - key: {}", key, e);
            return false;
        }
    }

    @Override
    public Boolean exists(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            log.trace("检查缓存存在性 - key: {}, exists: {}", key, exists);
            return exists;
        } catch (Exception e) {
            log.error("检查缓存失败 - key: {}", key, e);
            return false;
        }
    }

    @Override
    public Boolean expire(String key, long expireSeconds) {
        try {
            Boolean result = redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
            log.debug("设置过期时间 - key: {}, expire: {}s", key, expireSeconds);
            return result;
        } catch (Exception e) {
            log.error("设置过期时间失败 - key: {}", key, e);
            return false;
        }
    }

    @Override
    public Long increment(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().increment(key, delta);
            log.debug("原子递增 - key: {}, delta: {}, result: {}", key, delta, result);
            return result;
        } catch (Exception e) {
            log.error("原子递增失败 - key: {}", key, e);
            return null;
        }
    }

    @Override
    public Long decrement(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().decrement(key, delta);
            log.debug("原子递减 - key: {}, delta: {}, result: {}", key, delta, result);
            return result;
        } catch (Exception e) {
            log.error("原子递减失败 - key: {}", key, e);
            return null;
        }
    }

    @Override
    public Boolean setIfAbsent(String key, String value, long expireSeconds) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, expireSeconds, TimeUnit.SECONDS);
            log.debug("原子设置 (不存在) - key: {}, expire: {}s, result: {}", key, expireSeconds, result);
            return result;
        } catch (Exception e) {
            log.error("原子设置失败 - key: {}", key, e);
            return false;
        }
    }

    @Override
    public Long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            log.trace("获取剩余过期时间 - key: {}, expire: {}s", key, expire);
            return expire;
        } catch (Exception e) {
            log.error("获取过期时间失败 - key: {}", key, e);
            return -2L;
        }
    }
}