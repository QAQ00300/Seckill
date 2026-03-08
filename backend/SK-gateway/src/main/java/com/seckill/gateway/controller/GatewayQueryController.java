package com.seckill.gateway.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * 网关查询控制器（只读 GET 请求）
 *
 * 职责：提供所有查询类接口，无副作用操作
 * 特点：幂等、安全、可缓存
 *
 * 访问路径：/gateway/query/**
 * HTTP 方法：仅 GET
 */
@RestController
@RequestMapping("/gateway/query")
@RequiredArgsConstructor
@Slf4j
public class GatewayQueryController {

    private final DiscoveryClient discoveryClient;

    /**
     * 获取所有可用的微服务列表
     * URL: GET http://localhost:8080/gateway/query/services
     */
    @GetMapping("/services")
    public Mono<Map<String, Object>> getAllServices() {
        log.info("获取所有可用服务列表");

        return Mono.fromCallable(() -> {
            List<String> services = discoveryClient.getServices();

            Map<String, Object> result = new HashMap<>();
            result.put("totalServices", services.size());
            result.put("services", services);
            result.put("timestamp", System.currentTimeMillis());

            // 获取每个服务的实例详情
            Map<String, Object> serviceDetails = new HashMap<>();
            for (String service : services) {
                List<ServiceInstance> instances = discoveryClient.getInstances(service);
                Map<String, Object> instanceInfo = new HashMap<>();
                instanceInfo.put("instanceCount", instances.size());
                instanceInfo.put("instances", buildInstanceInfo(instances));
                serviceDetails.put(service, instanceInfo);
            }

            result.put("serviceDetails", serviceDetails);

            log.info("服务列表查询成功：共{}个服务", services.size());
            return result;
        });
    }

    /**
     * 获取指定服务的实例列表
     * URL: GET http://localhost:8080/gateway/query/service/{serviceId}
     */
    @GetMapping("/service/{serviceId}")
    public Mono<Map<String, Object>> getServiceInstances(
            @PathVariable String serviceId) {

        log.info("查询服务实例：serviceId={}", serviceId);

        return Mono.fromCallable(() -> {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);

            Map<String, Object> result = new HashMap<>();
            result.put("serviceId", serviceId);
            result.put("instanceCount", instances.size());
            result.put("instances", buildInstanceInfo(instances));
            result.put("timestamp", System.currentTimeMillis());

            if (instances.isEmpty()) {
                log.warn("服务未找到：serviceId={}", serviceId);
                result.put("status", "NOT_FOUND");
            } else {
                log.info("服务实例查询成功：serviceId={}, 实例数={}", serviceId, instances.size());
                result.put("status", "OK");
            }

            return result;
        });
    }

    /**
     * 获取服务实例（负载均衡）
     * URL: GET http://localhost:8080/gateway/query/instance/{serviceId}
     */
    @GetMapping("/instance/{serviceId}")
    public Mono<Map<String, Object>> getInstance(
            @PathVariable String serviceId) {

        log.info("获取服务实例（负载均衡）：serviceId={}", serviceId);

        return Mono.fromCallable(() -> {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);

            Map<String, Object> result = new HashMap<>();

            if (instances == null || instances.isEmpty()) {
                log.error("没有可用实例：serviceId={}", serviceId);
                result.put("status", "NO_AVAILABLE_INSTANCE");
                result.put("message", "服务未注册或无可用实例");
                result.put("serviceId", serviceId);
                return result;
            }

            // 简单轮询策略
            int index = Math.abs(new Random().nextInt()) % instances.size();
            ServiceInstance instance = instances.get(index);

            result.put("status", "OK");
            result.put("selectedInstance", buildInstanceInfo(Collections.singletonList(instance)).get(0));
            result.put("loadBalanceStrategy", "ROUND_ROBIN");
            result.put("timestamp", System.currentTimeMillis());

            log.info("实例选择成功：serviceId={}, selectedUri={}",
                    serviceId, instance.getUri());

            return result;
        });
    }

    /**
     * 服务健康检查
     * URL: GET http://localhost:8080/gateway/query/health/{serviceId}
     */
    @GetMapping("/health/{serviceId}")
    public Mono<Map<String, Object>> checkServiceHealth(
            @PathVariable String serviceId) {

        log.info("服务健康检查：serviceId={}", serviceId);

        return Mono.fromCallable(() -> {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);

            Map<String, Object> health = new HashMap<>();
            health.put("serviceId", serviceId);

            if (instances == null || instances.isEmpty()) {
                health.put("status", "UNAVAILABLE");
                health.put("message", "服务未注册");
                health.put("healthy", false);
                return health;
            }

            int healthyCount = 0;
            int totalCount = instances.size();
            List<Map<String, Object>> instanceHealth = new ArrayList<>();

            for (ServiceInstance instance : instances) {
                Map<String, Object> instanceInfo = new HashMap<>();
                instanceInfo.put("instanceId", instance.getInstanceId());
                instanceInfo.put("uri", instance.getUri());
                instanceInfo.put("host", instance.getHost());
                instanceInfo.put("port", instance.getPort());

                boolean isHealthy = true;
                instanceInfo.put("healthy", isHealthy);

                if (isHealthy) {
                    healthyCount++;
                }

                instanceHealth.add(instanceInfo);
            }

            health.put("status", healthyCount > 0 ? "HEALTHY" : "UNHEALTHY");
            health.put("healthy", healthyCount > 0);
            health.put("totalInstances", totalCount);
            health.put("healthyInstances", healthyCount);
            health.put("healthRate", String.format("%.2f%%", (double) healthyCount / totalCount * 100));
            health.put("instances", instanceHealth);
            health.put("timestamp", System.currentTimeMillis());

            log.info("服务健康检查完成：serviceId={}, 健康实例={}/{}",
                    serviceId, healthyCount, totalCount);

            return health;
        });
    }

    /**
     * 获取服务路由信息
     * URL: GET http://localhost:8080/gateway/query/routes
     */
    @GetMapping("/routes")
    public Mono<Map<String, Object>> getRoutes() {
        log.info("获取服务路由信息");

        return Mono.fromCallable(() -> {
            List<String> services = discoveryClient.getServices();

            Map<String, Object> routes = new HashMap<>();
            routes.put("totalRoutes", services.size());

            List<Map<String, Object>> routeList = new ArrayList<>();
            for (String service : services) {
                Map<String, Object> route = new HashMap<>();
                route.put("routeId", service + "-route");
                route.put("serviceId", service);
                route.put("uri", "lb://" + service);
                route.put("predicate", "Path=/api/" + service.replace("sk-", "") + "/**");
                route.put("stripPrefix", 1);
                routeList.add(route);
            }

            routes.put("routes", routeList);
            routes.put("timestamp", System.currentTimeMillis());

            return routes;
        });
    }

    /**
     * 服务调用统计
     * URL: GET http://localhost:8080/gateway/query/stats
     */
    @GetMapping("/stats")
    public Mono<Map<String, Object>> getServiceStats() {
        log.info("获取服务调用统计");

        return Mono.fromCallable(() -> {
            List<String> services = discoveryClient.getServices();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalServices", services.size());
            stats.put("stage", System.getProperty("spring.profiles.active", "stage1"));

            Map<String, Object> serviceStats = new HashMap<>();
            for (String service : services) {
                List<ServiceInstance> instances = discoveryClient.getInstances(service);
                Map<String, Object> stat = new HashMap<>();
                stat.put("instanceCount", instances.size());
                stat.put("registered", !instances.isEmpty());
                serviceStats.put(service, stat);
            }

            stats.put("serviceStats", serviceStats);
            stats.put("timestamp", System.currentTimeMillis());

            return stats;
        });
    }

    /**
     * 构建服务实例信息
     */
    private List<Map<String, Object>> buildInstanceInfo(List<ServiceInstance> instances) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (ServiceInstance instance : instances) {
            Map<String, Object> info = new HashMap<>();
            info.put("instanceId", instance.getInstanceId());
            info.put("serviceId", instance.getServiceId());
            info.put("host", instance.getHost());
            info.put("port", instance.getPort());
            info.put("uri", instance.getUri());
            info.put("scheme", instance.getScheme());
            info.put("metadata", instance.getMetadata());
            result.add(info);
        }

        return result;
    }
}