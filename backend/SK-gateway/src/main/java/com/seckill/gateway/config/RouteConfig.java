package com.seckill.gateway.config;



import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 路由配置类
 * 用于编程式配置路由规则
 */
@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 用户服务路由
                .route("user-service", r -> r
                        .path("/api/user/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Service", "user-service")
                                .addResponseHeader("X-Service", "user-service")
                                .circuitBreaker(config -> config
                                        .setName("user-service-cb")
                                        .setFallbackUri("forward:/fallback/user")))
                        .uri("lb://sk-user"))

                // 商品服务路由
                .route("product-service", r -> r
                        .path("/api/product/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Service", "product-service"))
                        .uri("lb://sk-product"))

                // 秒杀服务路由
                .route("seckill-service", r -> r
                        .path("/api/seckill/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Service", "seckill-service")
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())))
                        .uri("lb://sk-seckill"))

                // 订单服务路由
                .route("order-service", r -> r
                        .path("/api/order/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Service", "order-service"))
                        .uri("lb://sk-order"))

                // 测试服务路由
                .route("test-service", r -> r
                        .path("/api/test/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Test-Mode", "true"))
                        .uri("lb://sk-test"))

                .build();
    }

    /**
     * Redis限流器
     */
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(100, 200, 1);
    }
}