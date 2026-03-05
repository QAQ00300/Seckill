package com.seckill.gateway.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 请求日志过滤器
 * 记录完整的请求和响应日志（用于调试和测试分析）
 */
@Component
@Slf4j
public class RequestLogFilter implements GlobalFilter, Ordered {

    private final AtomicInteger requestCounter = new AtomicInteger(0);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 只记录API请求，不记录静态资源
        if (!shouldLog(request)) {
            return chain.filter(exchange);
        }

        int requestNumber = requestCounter.incrementAndGet();

        // 记录请求体（对于POST/PUT请求）
        if (request.getMethod() == HttpMethod.POST ||
                request.getMethod() == HttpMethod.PUT) {

            return logRequestBody(exchange, chain, requestNumber);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> logRequestBody(ServerWebExchange exchange,
                                      GatewayFilterChain chain,
                                      int requestNumber) {

        ServerHttpRequest request = exchange.getRequest();

        // 获取请求体
        Flux<DataBuffer> body = request.getBody();

        // 缓存请求体
        AtomicReference<StringBuilder> bodyRef = new AtomicReference<>(new StringBuilder());

        return body
                .map(buffer -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .collectList()
                .flatMap(contents -> {
                    String requestBody = String.join("", contents);
                    bodyRef.get().append(requestBody);

                    // 创建新的请求，包含缓存的body
                    ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(request) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return Flux.just(requestBody)
                                    .map(s -> exchange.getResponse().bufferFactory()
                                            .wrap(s.getBytes(StandardCharsets.UTF_8)));
                        }
                    };

                    // 记录请求日志
                    log.info("请求[{}] - 方法: {}, 路径: {}, 请求体: {}",
                            requestNumber,
                            request.getMethod(),
                            request.getPath(),
                            requestBody.length() > 1000 ?
                                    requestBody.substring(0, 1000) + "..." :
                                    requestBody);

                    return chain.filter(exchange.mutate()
                            .request(mutatedRequest)
                            .build());
                });
    }

    private boolean shouldLog(ServerHttpRequest request) {
        String path = request.getPath().toString();
        return !path.contains("/actuator") &&
                !path.contains("/swagger") &&
                !path.contains("/api-docs") &&
                !path.contains("/favicon.ico");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
}