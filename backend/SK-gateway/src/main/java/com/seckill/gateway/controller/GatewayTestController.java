package com.seckill.gateway.controller;



import com.seckill.gateway.model.PerformanceMetrics;
import com.seckill.gateway.service.MetricsCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网关测试控制器
 * 用于测试网关功能和获取测试数据
 *
 * 测试接口列表：
 * - GET  /gateway/test/ping      - 测试网关连通性
 * - GET  /gateway/test/metrics   - 获取性能指标
 * - GET  /gateway/test/slow      - 模拟慢请求
 * - GET  /gateway/test/error     - 模拟错误请求
 * - POST /gateway/test/data      - 生成测试数据
 */
@RestController
@RequestMapping("/gateway/test")
@Slf4j
public class GatewayTestController {

    @Autowired
    private MetricsCollector metricsCollector;

    /**
     * 1. 测试网关连通性
     * URL: GET http://localhost:8080/gateway/test/ping
     */
    @GetMapping("/ping")
    public Mono<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "网关服务正常运行");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");

        // 添加请求头信息
        response.put("server", "SK-Gateway");
        response.put("time", LocalDateTime.now().toString());

        return Mono.just(response);
    }

    /**
     * 2. 获取性能指标
     * URL: GET http://localhost:8080/gateway/test/metrics/{stage}
     * 例如: GET http://localhost:8080/gateway/test/metrics/stage1
     */
    @GetMapping("/metrics/{stage}")
    public Mono<Map<String, Object>> getMetrics(@PathVariable String stage) {
        return Mono.fromCallable(() -> {
            Map<String, Object> result = new HashMap<>();

            // 获取指定阶段的性能指标
            List<PerformanceMetrics> metrics = metricsCollector.getMetrics(stage);

            if (metrics == null || metrics.isEmpty()) {
                result.put("stage", stage);
                result.put("message", "暂无数据");
                result.put("timestamp", System.currentTimeMillis());
                return result;
            }

            // 计算统计信息
            double avgResponseTime = metrics.stream()
                    .mapToLong(PerformanceMetrics::getDuration)
                    .average()
                    .orElse(0);

            long maxResponseTime = metrics.stream()
                    .mapToLong(PerformanceMetrics::getDuration)
                    .max()
                    .orElse(0);

            long minResponseTime = metrics.stream()
                    .mapToLong(PerformanceMetrics::getDuration)
                    .min()
                    .orElse(0);

            long successCount = metrics.stream()
                    .filter(PerformanceMetrics::isSuccess)
                    .count();

            // 填充结果
            result.put("stage", stage);
            result.put("totalRequests", metrics.size());
            result.put("successRequests", successCount);
            result.put("failedRequests", metrics.size() - successCount);
            result.put("successRate", String.format("%.2f%%", (double) successCount / metrics.size() * 100));
            result.put("avgResponseTime", String.format("%.2fms", avgResponseTime));
            result.put("maxResponseTime", maxResponseTime + "ms");
            result.put("minResponseTime", minResponseTime + "ms");
            result.put("timestamp", System.currentTimeMillis());

            return result;
        });
    }

    /**
     * 3. 模拟慢请求（用于测试网关超时和熔断）
     * URL: GET http://localhost:8080/gateway/test/slow?delay=2000
     */
    @GetMapping("/slow")
    public Mono<Map<String, Object>> slowRequest(
            @RequestParam(defaultValue = "2000") long delay) {

        log.info("收到慢请求: delay={}ms", delay);

        return Mono.delay(Duration.ofMillis(delay))
                .map(t -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "slow_response");
                    response.put("delay", delay + "ms");
                    response.put("message", "这是一个模拟的慢请求");
                    response.put("timestamp", System.currentTimeMillis());
                    return response;
                });
    }

    /**
     * 4. 模拟错误请求（用于测试异常处理）
     * URL: GET http://localhost:8080/gateway/test/error?type=500
     */
    @GetMapping("/error")
    public Mono<Map<String, Object>> errorRequest(
            @RequestParam(defaultValue = "500") int code) {

        log.info("收到错误请求: code={}", code);

        // 根据错误码返回不同错误
        switch (code) {
            case 400:
                return Mono.error(new IllegalArgumentException("参数错误"));
            case 404:
                return Mono.error(new RuntimeException("资源不存在"));
            case 500:
                return Mono.error(new RuntimeException("服务器内部错误"));
            case 503:
                return Mono.error(new RuntimeException("服务不可用"));
            default:
                return Mono.error(new RuntimeException("未知错误"));
        }
    }

    /**
     * 5. 生成测试数据
     * URL: POST http://localhost:8080/gateway/test/data
     * Body: {"stage":"stage1", "count":100}
     */
    @PostMapping("/data")
    public Mono<Map<String, Object>> generateTestData(@RequestBody TestDataRequest request) {
        log.info("生成测试数据: stage={}, count={}", request.getStage(), request.getCount());

        return Mono.fromCallable(() -> {
            List<PerformanceMetrics> metrics = metricsCollector.generateTestData(
                    request.getStage(),
                    request.getCount()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("stage", request.getStage());
            response.put("generated", metrics.size());
            response.put("message", "测试数据生成成功");
            response.put("timestamp", System.currentTimeMillis());

            return response;
        });
    }

    /**
     * 6. 获取网关信息
     * URL: GET http://localhost:8080/gateway/test/info
     */
    @GetMapping("/info")
    public Mono<Map<String, Object>> getGatewayInfo() {
        Map<String, Object> info = new HashMap<>();

        info.put("application", "SK-Gateway");
        info.put("version", "1.0.0");
        info.put("java.version", System.getProperty("java.version"));
        info.put("os.name", System.getProperty("os.name"));
        info.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        info.put("freeMemory", Runtime.getRuntime().freeMemory() / 1024 / 1024 + "MB");
        info.put("totalMemory", Runtime.getRuntime().totalMemory() / 1024 / 1024 + "MB");
        info.put("maxMemory", Runtime.getRuntime().maxMemory() / 1024 / 1024 + "MB");
        info.put("timestamp", System.currentTimeMillis());

        return Mono.just(info);
    }

    /**
     * 7. 重置测试数据
     * URL: DELETE http://localhost:8080/gateway/test/metrics/{stage}
     */
    @DeleteMapping("/metrics/{stage}")
    public Mono<Map<String, Object>> resetMetrics(@PathVariable String stage) {
        metricsCollector.resetMetrics(stage);

        Map<String, Object> response = new HashMap<>();
        response.put("stage", stage);
        response.put("message", "性能指标已重置");
        response.put("timestamp", System.currentTimeMillis());

        return Mono.just(response);
    }
}

