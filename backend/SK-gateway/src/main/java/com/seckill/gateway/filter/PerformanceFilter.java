package com.seckill.gateway.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能监控过滤器
 * 记录每个请求的处理时间、TPS等指标
 */
@Component
@Slf4j
public class PerformanceFilter implements GlobalFilter, Ordered {

    // 请求计数器
    private final AtomicLong requestCounter = new AtomicLong(0);

    // 性能指标缓存
    private final Map<String, List<Long>> performanceMetrics = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 1. 生成请求ID
        String requestId = generateRequestId();
        long startTime = System.nanoTime();
        long requestNumber = requestCounter.incrementAndGet();

        // 2. 添加请求头
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-Request-Id", requestId)
                .header("X-Request-Number", String.valueOf(requestNumber))
                .header("X-Start-Time", String.valueOf(System.currentTimeMillis()))
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        // 3. 获取阶段信息
        String[] stageHolder = new String[1];
        stageHolder[0] = request.getHeaders().getFirst("X-Stage");
        if (stageHolder[0] == null || stageHolder[0].isEmpty()) {
            stageHolder[0] = System.getProperty("spring.profiles.active", "stage1");
        }
        // 4. 记录请求开始
        logRequestStart(mutatedRequest, requestNumber, stageHolder[0]);

        // 5. 执行后续过滤器链
        return chain.filter(mutatedExchange)
                .doOnSuccess(v -> handleSuccess(mutatedRequest, response, startTime, requestId, stageHolder[0]))
                .doOnError(e -> handleError(mutatedRequest, response, startTime, requestId, stageHolder[0], e))
                .then(Mono.fromRunnable(() -> {
                    // 6. 记录请求结束
                    long duration = (System.nanoTime() - startTime) / 1_000_000; // 转换为毫秒
                    logRequestEnd(mutatedRequest, response, duration, requestNumber, stageHolder[0]);

                    // 7. 收集性能指标
                    collectPerformanceMetrics(request.getPath().toString(), duration, stageHolder[0]);
                }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;  // 最高优先级，最先执行
    }

    private String generateRequestId() {
        return String.format("req_%d_%d", System.currentTimeMillis(),
                (int)(Math.random() * 10000));
    }

    private void logRequestStart(ServerHttpRequest request, long requestNumber, String stage) {
        log.info("\n========== 请求开始 ==========\n" +
                        "请求号: {}\n" +
                        "阶段: {}\n" +
                        "时间: {}\n" +
                        "方法: {}\n" +
                        "路径: {}\n" +
                        "参数: {}\n" +
                        "客户端IP: {}\n" +
                        "用户代理: {}\n" +
                        "================================",
                requestNumber,
                stage,
                LocalDateTime.now(),
                request.getMethod(),
                request.getPath(),
                request.getQueryParams(),
                request.getRemoteAddress(),
                request.getHeaders().getFirst("User-Agent"));
    }

    private void logRequestEnd(ServerHttpRequest request, ServerHttpResponse response,
                               long duration, long requestNumber, String stage) {

        String logLevel = getLogLevel(duration);

        switch (logLevel) {
            case "WARN":
                log.warn("\n========== 请求结束 ==========\n" +
                                "请求号: {}\n" +
                                "阶段: {}\n" +
                                "状态: {}\n" +
                                "耗时: {}ms\n" +
                                "================================",
                        requestNumber, stage, response.getStatusCode(), duration);
                break;
            case "ERROR":
                log.error("\n========== 请求结束 ==========\n" +
                                "请求号: {}\n" +
                                "阶段: {}\n" +
                                "状态: {}\n" +
                                "耗时: {}ms\n" +
                                "================================",
                        requestNumber, stage, response.getStatusCode(), duration);
                break;
            default:
                log.info("\n========== 请求结束 ==========\n" +
                                "请求号: {}\n" +
                                "阶段: {}\n" +
                                "状态: {}\n" +
                                "耗时: {}ms\n" +
                                "================================",
                        requestNumber, stage, response.getStatusCode(), duration);
        }
    }

    private String getLogLevel(long duration) {
        if (duration > 3000) return "ERROR";
        if (duration > 1000) return "WARN";
        return "INFO";
    }

    private void handleSuccess(ServerHttpRequest request, ServerHttpResponse response,
                               long startTime, String requestId, String stage) {
        long duration = (System.nanoTime() - startTime) / 1_000_000;

        // 添加性能响应头
        response.getHeaders().add("X-Response-Time", String.valueOf(duration));
        response.getHeaders().add("X-Stage", stage);
        response.getHeaders().add("X-Request-Id", requestId);
    }

    private void collectPerformanceMetrics(String path, long duration, String stage) {
        performanceMetrics.computeIfAbsent(stage, k -> new ArrayList<>()).add(duration);

        // 每分钟输出一次统计信息
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            List<Long> metrics = performanceMetrics.get(stage);
            if (metrics != null && !metrics.isEmpty()) {
                LongSummaryStatistics stats = metrics.stream()
                        .mapToLong(Long::longValue)
                        .summaryStatistics();

                log.info("性能统计[{}] - 请求数: {}, 平均耗时: {:.2f}ms, 最大耗时: {}ms, 最小耗时: {}ms",
                        stage, stats.getCount(), stats.getAverage(),
                        stats.getMax(), stats.getMin());

                metrics.clear();
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    private void handleError(ServerHttpRequest request, ServerHttpResponse response,
                              long startTime, String requestId, String stage, Throwable e) {
        log.error("请求处理失败: requestId={}, stage={}, error={}",
                requestId, stage, e.getMessage());

        response.getHeaders().add("X-Error", e.getMessage());
    }
}