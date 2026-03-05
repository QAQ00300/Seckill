package com.seckill.gateway.handler;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import com.alibaba.fastjson.JSON;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@Order(-1)
@Configuration
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        // 设置响应头
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status = determineStatus(ex);
        response.setStatusCode(status);

        // 构建错误响应
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", status.value());
        errorResponse.put("message", determineMessage(ex));
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("path", exchange.getRequest().getPath().toString());

        // 添加请求ID
        String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-Id");
        if (requestId != null) {
            errorResponse.put("requestId", requestId);
        }

        // 添加阶段信息
        String stage = exchange.getRequest().getHeaders().getFirst("X-Stage");
        if (stage != null) {
            errorResponse.put("stage", stage);
        }

        // 转换为JSON
        byte[] bytes = JSON.toJSONString(errorResponse).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }

    private HttpStatus determineStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return (HttpStatus) ((ResponseStatusException) ex).getStatusCode();

        }
        if (ex instanceof NotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String determineMessage(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return ((ResponseStatusException) ex).getReason() != null ?
                    ((ResponseStatusException) ex).getReason() : "请求处理失败";
        }
        if (ex instanceof NotFoundException) {
            return "服务未找到";
        }
        return "系统内部错误";
    }
}