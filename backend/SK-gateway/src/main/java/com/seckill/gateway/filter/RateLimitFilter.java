package com.seckill.gateway.filter;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * 使用 Redis 进行分布式限流
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    private final com.seckill.gateway.config.GatewayProperties properties;

    private static final String RATE_LIMIT_KEY_PREFIX = "rate:limit:";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.getRateLimit().isEnabled()) {
            log.debug("限流功能未启用，直接放行");
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();

        String clientIp = request.getRemoteAddress() != null ?
                request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        String userId = request.getHeaders().getFirst("X-User-Id");
        String limitKey = userId != null ? userId : clientIp;

        int windowSize = properties.getRateLimit().getDefaultWindow();
        long currentSecond = Instant.now().getEpochSecond();
        String redisKey = RATE_LIMIT_KEY_PREFIX + limitKey + ":" + (currentSecond / windowSize);

        return redisTemplate.opsForValue()
                .increment(redisKey)
                .flatMap(count -> {
                    if (count == 1) {
                        return redisTemplate.expire(redisKey, Duration.ofSeconds(windowSize))
                                .thenReturn(count);
                    }
                    return Mono.just(count);
                })
                .flatMap(count -> {
                    int limit = properties.getRateLimit().getDefaultLimit();

                    if (count > limit) {
                        log.warn("请求被限流：key={}, count={}, limit={}", limitKey, count, limit);
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        exchange.getResponse().getHeaders().add("X-RateLimit-Reset",
                                String.valueOf(Instant.now().getEpochSecond() + windowSize));
                        exchange.getResponse().getHeaders().add("X-RateLimit-Limit",
                                String.valueOf(limit));
                        exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", "0");

                        return exchange.getResponse().setComplete();
                    }

                    exchange.getResponse().getHeaders().add("X-RateLimit-Limit",
                            String.valueOf(limit));
                    exchange.getResponse().getHeaders().add("X-RateLimit-Remaining",
                            String.valueOf(limit - count));

                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}