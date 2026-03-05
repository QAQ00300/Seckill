package com.seckill.gateway.filter;


import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 测试头过滤器
 * 为测试请求添加必要的头信息
 */
@Component
public class TestHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. 检查是否是测试请求
        String testMode = request.getHeaders().getFirst("X-Test-Mode");
        String stage = request.getHeaders().getFirst("X-Stage");

        // 如果是测试请求，添加额外的测试头
        if ("true".equals(testMode) || stage != null) {
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-Performance-Test", "true")
                    .header("X-Test-Timestamp", String.valueOf(System.currentTimeMillis()))
                    .header("X-Test-Round", getTestRound())
                    .build();

            // 添加测试专用的响应头
            exchange.getResponse().getHeaders().add("X-Test-Enabled", "true");

            return chain.filter(exchange.mutate()
                    .request(mutatedRequest)
                    .build());
        }

        return chain.filter(exchange);
    }

    private String getTestRound() {
        // 从配置或缓存中获取当前测试轮次
        return "1";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 4;
    }
}