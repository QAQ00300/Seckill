package com.seckill.gateway.filter;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * 限流过滤器
 * 使用Redis进行分布式限流
 */
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private static final String RATE_LIMIT_KEY_PREFIX = "rate:limit:";
    private static final int DEFAULT_LIMIT = 100;  // 默认每秒100个请求
    private static final int DEFAULT_WINDOW = 1;   // 时间窗口（秒）

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. 获取限流key（可以基于IP、用户ID等）
        String clientIp = request.getRemoteAddress() != null ?
                request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        String userId = request.getHeaders().getFirst("X-User-Id");
        String limitKey = userId != null ? userId : clientIp;

        String redisKey = RATE_LIMIT_KEY_PREFIX + limitKey + ":" +
                Instant.now().getEpochSecond() / DEFAULT_WINDOW;

        // 2. Redis原子操作：计数并检查是否超过限制
        return redisTemplate.opsForValue()
                .increment(redisKey)
                .flatMap(count -> {
                    // 设置过期时间
                    if (count == 1) {
                        return redisTemplate.expire(redisKey, Duration.ofSeconds(DEFAULT_WINDOW))
                                .thenReturn(count);
                    }
                    return Mono.just(count);
                })
                .flatMap(count -> {
                    if (count > DEFAULT_LIMIT) {
                        // 超过限流，返回429
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        exchange.getResponse().getHeaders().add("X-RateLimit-Reset",
                                String.valueOf(Instant.now().getEpochSecond() + DEFAULT_WINDOW));

                        // 返回错误信息
                        return exchange.getResponse().setComplete();
                    }

                    // 添加限流头信息
                    exchange.getResponse().getHeaders().add("X-RateLimit-Limit",
                            String.valueOf(DEFAULT_LIMIT));
                    exchange.getResponse().getHeaders().add("X-RateLimit-Remaining",
                            String.valueOf(DEFAULT_LIMIT - count));

                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 3;
    }
}