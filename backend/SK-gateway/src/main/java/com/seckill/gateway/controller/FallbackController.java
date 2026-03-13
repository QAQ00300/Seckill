package com.seckill.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    /**
     * 通用熔断降级处理
     */
    @GetMapping(path = "/fallback/{service}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> fallback(@PathVariable String service) {
        Map<String, Object> result = new HashMap<>(6);
        result.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        result.put("message", service + "服务暂时不可用，请稍后重试");
        result.put("service", service);
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("type", "circuit_breaker_fallback");
        result.put("suggestion", "系统正在保护中，建议稍后重试或联系管理员");

        return Mono.just(result);
    }

    /**
     * 用户服务降级
     */
    @GetMapping(path = "/fallback/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> userFallback() {
        Map<String, Object> result = new HashMap<>(6);
        result.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        result.put("message", "用户服务繁忙，请稍后重试");
        result.put("service", "user");
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("type", "circuit_breaker_fallback");
        result.put("suggestion", "您可以先浏览其他内容");

        return Mono.just(result);
    }

    /**
     * 订单服务降级
     */
    @GetMapping(path = "/fallback/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> orderFallback() {
        Map<String, Object> result = new HashMap<>(6);
        result.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        result.put("message", "订单服务繁忙，请稍后重试");
        result.put("service", "order");
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("type", "circuit_breaker_fallback");
        result.put("suggestion", "请检查网络或稍后重试");

        return Mono.just(result);
    }

    /**
     * 秒杀服务降级
     */
    @GetMapping(path = "/fallback/seckill", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> seckillFallback() {
        Map<String, Object> result = new HashMap<>(6);
        result.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        result.put("message", "秒杀活动太火爆，系统正在保护中");
        result.put("service", "seckill");
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("type", "circuit_breaker_fallback");
        result.put("suggestion", "请刷新页面重新尝试");

        return Mono.just(result);
    }
}