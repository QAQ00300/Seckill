package com.seckill.gateway.service;

import com.seckill.gateway.model.PerformanceMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*; /**
 * 性能指标收集器
 */
@Component
@Slf4j
public class MetricsCollector {

    private final Map<String, List<PerformanceMetrics>> metricsMap = new ConcurrentHashMap<>();


    private final Random random = new Random();

    public void collect(PerformanceMetrics metrics) {
        String stage = metrics.getStage();
        metricsMap.computeIfAbsent(stage, k -> new CopyOnWriteArrayList<>()).add(metrics);

        // 每分钟输出一次统计
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            List<PerformanceMetrics> stageMetrics = metricsMap.get(stage);
            if (stageMetrics != null && !stageMetrics.isEmpty()) {
                generateReport(stage, stageMetrics);
                stageMetrics.clear();  // 清空已统计的数据
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    private void generateReport(String stage, List<PerformanceMetrics> metrics) {
        DoubleSummaryStatistics stats = metrics.stream()
                .mapToDouble(PerformanceMetrics::getDuration)
                .summaryStatistics();

        long successCount = metrics.stream().filter(PerformanceMetrics::isSuccess).count();

        log.info("\n========== 网关性能报告 [{}] ==========\n" +
                        "总请求数: {}\n" +
                        "成功请求: {}\n" +
                        "失败请求: {}\n" +
                        "成功率: {:.2f}%\n" +
                        "平均耗时: {:.2f}ms\n" +
                        "最大耗时: {}ms\n" +
                        "最小耗时: {}ms\n" +
                        "TPS: {:.2f}\n" +
                        "========================================",
                stage,
                metrics.size(),
                successCount,
                metrics.size() - successCount,
                (double) successCount / metrics.size() * 100,
                stats.getAverage(),
                stats.getMax(),
                stats.getMin(),
                calculateTPS(metrics));
    }

    private double calculateTPS(List<PerformanceMetrics> metrics) {
        if (metrics.isEmpty()) return 0;

        long startTime = metrics.stream()
                .mapToLong(PerformanceMetrics::getTimestamp)
                .min().orElse(0);
        long endTime = metrics.stream()
                .mapToLong(PerformanceMetrics::getTimestamp)
                .max().orElse(0);

        long durationSeconds = (endTime - startTime) / 1000;
        return durationSeconds > 0 ? (double) metrics.size() / durationSeconds : 0;
    }

    public List<PerformanceMetrics> getMetrics(String stage) {
        log.debug("获取性能指标：stage={}", stage);

        if ("all".equalsIgnoreCase(stage)) {
            // 返回所有阶段的数据
            List<PerformanceMetrics> allMetrics = new ArrayList<>();
            metricsMap.values().forEach(allMetrics::addAll);
            log.debug("获取所有阶段的性能指标，共 {} 条数据", allMetrics.size());
            return allMetrics;
        } else {
            // 返回指定阶段的数据
            List<PerformanceMetrics> stageMetrics = metricsMap.get(stage);
            int size = (stageMetrics != null) ? stageMetrics.size() : 0;
            log.debug("获取阶段 [{}] 的性能指标，共 {} 条数据", stage, size);
            return stageMetrics;
        }
    }


    public List<PerformanceMetrics> generateTestData(String stage, int count) {
        log.info("生成测试数据：stage={}, count={}", stage, count);

        List<PerformanceMetrics> testData = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            boolean isSuccess = random.nextDouble() > 0.1; // 90% 成功率

            PerformanceMetrics metrics = PerformanceMetrics.builder()
                    .requestId("test-" + stage + "-" + System.currentTimeMillis() + "-" + i)
                    .stage(stage)
                    .api("/api/v1/test/" + stage)
                    .method(i % 2 == 0 ? "GET" : "POST")
                    .status(isSuccess ? 200 : (random.nextBoolean() ? 400 : 500))
                    .duration(50L + random.nextInt(500)) // 50-550ms 随机响应时间
                    .timestamp(System.currentTimeMillis() - random.nextInt(3600000)) // 过去 1 小时内
                    .success(isSuccess)
                    .clientIp("192.168.1." + (random.nextInt(254) + 1))
                    .userId("user_" + (1000 + random.nextInt(9000)))
                    .errorMsg(isSuccess ? null : "模拟错误：" + (isSuccess ? "" : "测试失败"))
                    .build();

            testData.add(metrics);
        }

        metricsMap.put(stage, new CopyOnWriteArrayList<>(testData));

        log.info("成功生成 {} 条测试数据，已存入 metricsMap", testData.size());

        return testData;
    }

    public void resetMetrics(String stage) {
        log.info("重置性能指标：stage={}", stage);

        if ("all".equalsIgnoreCase(stage)) {
            // 重置所有阶段的数据
            int totalCleared = metricsMap.values().stream()
                    .mapToInt(List::size)
                    .sum();
            metricsMap.clear();
            log.info("已重置所有阶段的性能指标，共清除 {} 条数据", totalCleared);
        } else {
            // 重置指定阶段的数据
            List<PerformanceMetrics> removed = metricsMap.remove(stage);
            int cleared = (removed != null) ? removed.size() : 0;
            log.info("已重置阶段 [{}] 的性能指标，共清除 {} 条数据", stage, cleared);
        }
    }
}
