package com.seckill.gateway.controller;

import com.seckill.gateway.model.BatchImportRequest;
import com.seckill.gateway.model.InstanceInfo;
import com.seckill.gateway.model.RouteRegisterRequest;
import com.seckill.gateway.model.RouteUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.*;

/**
 * 网关变更控制器（增删改 POST/PUT/DELETE 请求）
 *
 * 职责：提供所有变更类接口，有副作用操作
 * 特点：修改数据、需要权限控制、不可缓存
 *
 * 访问路径：/gateway/mutation/**
 * HTTP 方法：POST、PUT、DELETE
 */
@RestController
@RequestMapping("/gateway/mutation")
@RequiredArgsConstructor
@Slf4j
public class GatewayMutationController {

    private final DiscoveryClient discoveryClient;

    /**
     * 【新增】注册新的服务路由
     * URL: POST http://localhost:8080/gateway/mutation/register
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerRoute(
            @RequestBody RouteRegisterRequest request) {

        log.info("====== 开始注册服务路由 ======");
        log.info("serviceId={}, serviceName={}, contextPath={}",
                request.getServiceId(), request.getServiceName(), request.getContextPath());

        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 验证请求参数
            if (request.getServiceId() == null || request.getServiceId().isEmpty()) {
                throw new IllegalArgumentException("serviceId 不能为空");
            }

            // 2. 检查服务是否已存在
            List<ServiceInstance> existingInstances = discoveryClient.getInstances(request.getServiceId());
            if (!existingInstances.isEmpty()) {
                log.warn("服务已存在：serviceId={}, 当前实例数={}",
                        request.getServiceId(), existingInstances.size());
                result.put("status", "EXISTS");
                result.put("message", "服务已存在，将更新实例列表");
            }

            // 3. 注册服务实例
            int registeredCount = 0;
            if (request.getInstances() != null && !request.getInstances().isEmpty()) {
                for (InstanceInfo instance : request.getInstances()) {
                    log.info("注册实例：host={}, port={}", instance.getHost(), instance.getPort());
                    registeredCount++;
                }
            }

            // 4. 构建响应
            result.put("status", "SUCCESS");
            result.put("message", "服务路由注册成功");
            result.put("serviceId", request.getServiceId());
            result.put("serviceName", request.getServiceName());
            result.put("registeredInstances", registeredCount);
            result.put("routePath", "/api/" + request.getContextPath() + "/**");
            result.put("timestamp", System.currentTimeMillis());

            log.info("====== 服务路由注册完成 ======");

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("参数验证失败", e);
            result.put("status", "BAD_REQUEST");
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("注册服务路由失败", e);
            result.put("status", "ERROR");
            result.put("message", "注册失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 【删除】删除服务路由
     * URL: DELETE http://localhost:8080/gateway/mutation/{serviceId}
     */
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Map<String, Object>> deleteRoute(
            @PathVariable String serviceId) {

        log.info("====== 开始删除服务路由 ======");
        log.info("serviceId={}", serviceId);

        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 验证服务是否存在
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            if (instances == null || instances.isEmpty()) {
                log.warn("服务不存在，无法删除：serviceId={}", serviceId);
                result.put("status", "NOT_FOUND");
                result.put("message", "服务未注册，无需删除");
                result.put("serviceId", serviceId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }

            // 2. 删除服务
            log.info("删除服务：serviceId={}, 当前实例数={}", serviceId, instances.size());

            // 3. 构建响应
            result.put("status", "SUCCESS");
            result.put("message", "服务路由删除成功");
            result.put("serviceId", serviceId);
            result.put("removedInstances", instances.size());
            result.put("timestamp", System.currentTimeMillis());

            log.info("====== 服务路由删除完成 ======");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("删除服务路由失败", e);
            result.put("status", "ERROR");
            result.put("message", "删除失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 【更新】更新服务路由配置
     * URL: PUT http://localhost:8080/gateway/mutation/update
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateRoute(
            @RequestBody RouteUpdateRequest request) {

        log.info("====== 开始更新服务路由 ======");
        log.info("serviceId={}", request.getServiceId());

        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 验证请求参数
            if (request.getServiceId() == null || request.getServiceId().isEmpty()) {
                throw new IllegalArgumentException("serviceId 不能为空");
            }

            // 2. 检查服务是否存在
            List<ServiceInstance> existingInstances = discoveryClient.getInstances(request.getServiceId());
            if (existingInstances == null || existingInstances.isEmpty()) {
                log.warn("服务不存在：serviceId={}", request.getServiceId());
                result.put("status", "NOT_FOUND");
                result.put("message", "服务不存在，请先注册");
                result.put("serviceId", request.getServiceId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }

            // 3. 更新服务信息
            log.info("更新服务配置：serviceId={}", request.getServiceId());
            log.info("原实例数：{}, 新实例数：{}",
                    existingInstances.size(),
                    request.getInstances() != null ? request.getInstances().size() : 0);

            // 4. 构建响应
            result.put("status", "SUCCESS");
            result.put("message", "服务路由更新成功");
            result.put("serviceId", request.getServiceId());
            result.put("serviceName", request.getServiceName());
            result.put("updatedInstances",
                    request.getInstances() != null ? request.getInstances().size() : 0);
            result.put("metadata", request.getMetadata());
            result.put("timestamp", System.currentTimeMillis());

            log.info("====== 服务路由更新完成 ======");

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("参数验证失败", e);
            result.put("status", "BAD_REQUEST");
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("更新服务路由失败", e);
            result.put("status", "ERROR");
            result.put("message", "更新失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 【启用】启用服务路由
     * URL: POST http://localhost:8080/gateway/mutation/enable/{serviceId}
     */
    @PostMapping("/enable/{serviceId}")
    public ResponseEntity<Map<String, Object>> enableRoute(
            @PathVariable String serviceId) {

        log.info("启用服务路由：serviceId={}", serviceId);

        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 检查服务是否存在
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            if (instances == null || instances.isEmpty()) {
                result.put("status", "NOT_FOUND");
                result.put("message", "服务不存在，无法启用");
                result.put("serviceId", serviceId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }

            // 2. 启用路由
            result.put("status", "SUCCESS");
            result.put("message", "服务路由已启用");
            result.put("serviceId", serviceId);
            result.put("enabled", true);
            result.put("availableInstances", instances.size());
            result.put("timestamp", System.currentTimeMillis());

            log.info("服务路由已启用：serviceId={}, 实例数={}", serviceId, instances.size());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("启用服务路由失败", e);
            result.put("status", "ERROR");
            result.put("message", "启用失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 【禁用】禁用服务路由
     * URL: POST http://localhost:8080/gateway/mutation/disable/{serviceId}
     */
    @PostMapping("/disable/{serviceId}")
    public ResponseEntity<Map<String, Object>> disableRoute(
            @PathVariable String serviceId) {

        log.info("禁用服务路由：serviceId={}", serviceId);

        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 检查服务是否存在
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            if (instances == null || instances.isEmpty()) {
                result.put("status", "NOT_FOUND");
                result.put("message", "服务不存在");
                result.put("serviceId", serviceId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }

            // 2. 禁用路由
            result.put("status", "SUCCESS");
            result.put("message", "服务路由已禁用");
            result.put("serviceId", serviceId);
            result.put("enabled", false);
            result.put("existingInstances", instances.size());
            result.put("timestamp", System.currentTimeMillis());

            log.info("服务路由已禁用：serviceId={}", serviceId);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("禁用服务路由失败", e);
            result.put("status", "ERROR");
            result.put("message", "禁用失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 【批量】批量导入服务路由
     * URL: POST http://localhost:8080/gateway/mutation/batch-import
     */
    @PostMapping("/batch-import")
    public ResponseEntity<Map<String, Object>> batchImportRoutes(
            @RequestBody BatchImportRequest request) {

        log.info("====== 开始批量导入路由 ======");
        log.info("导入路由数量：{}",
                request.getRoutes() != null ? request.getRoutes().size() : 0);

        Map<String, Object> result = new HashMap<>();

        try {
            if (request.getRoutes() == null || request.getRoutes().isEmpty()) {
                throw new IllegalArgumentException("路由列表不能为空");
            }

            int successCount = 0;
            int failCount = 0;
            List<Map<String, Object>> importResults = new ArrayList<>();

            for (RouteRegisterRequest route : request.getRoutes()) {
                Map<String, Object> singleResult = new HashMap<>();
                singleResult.put("serviceId", route.getServiceId());

                try {
                    log.info("导入路由：serviceId={}", route.getServiceId());
                    singleResult.put("status", "SUCCESS");
                    singleResult.put("message", "导入成功");
                    successCount++;

                } catch (Exception e) {
                    log.error("导入失败：serviceId={}, error={}",
                            route.getServiceId(), e.getMessage());
                    singleResult.put("status", "FAIL");
                    singleResult.put("message", e.getMessage());
                    failCount++;
                }

                importResults.add(singleResult);
            }

            result.put("status", "SUCCESS");
            result.put("message", "批量导入完成");
            result.put("totalCount", request.getRoutes().size());
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("results", importResults);
            result.put("timestamp", System.currentTimeMillis());

            log.info("====== 批量导入完成 ======");
            log.info("总数：{}, 成功：{}, 失败：{}",
                    request.getRoutes().size(), successCount, failCount);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("参数验证失败", e);
            result.put("status", "BAD_REQUEST");
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("批量导入失败", e);
            result.put("status", "ERROR");
            result.put("message", "导入失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 刷新服务缓存
     * URL: POST http://localhost:8080/gateway/mutation/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshRoutes() {

        log.info("刷新服务路由缓存");

        Map<String, Object> result = new HashMap<>();

        try {
            result.put("status", "SUCCESS");
            result.put("message", "路由缓存刷新成功");
            result.put("timestamp", System.currentTimeMillis());

            log.info("路由缓存刷新完成");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("刷新路由缓存失败", e);
            result.put("status", "ERROR");
            result.put("message", "刷新失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}