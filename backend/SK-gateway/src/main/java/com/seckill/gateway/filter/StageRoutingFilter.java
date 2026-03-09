package com.seckill.gateway.filter;



import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 阶段路由过滤器
 * 根据请求头中的X-Stage将请求路由到不同版本的服务
 */
@Component
public class StageRoutingFilter implements GlobalFilter, Ordered {

    private static final Map<String, String> STAGE_VERSION_MAP = new HashMap<>();

    static {
        STAGE_VERSION_MAP.put("stage1", "v1");
        STAGE_VERSION_MAP.put("stage2", "v2");
        STAGE_VERSION_MAP.put("stage3", "v3");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String stage = request.getHeaders().getFirst("X-Stage");

        if (stage == null || stage.isEmpty()) {
            stage = System.getProperty("spring.profiles.active", "stage1");
        }

        String version = STAGE_VERSION_MAP.getOrDefault(stage, "v1");

        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-Stage", stage)
                .header("X-Version", version)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}