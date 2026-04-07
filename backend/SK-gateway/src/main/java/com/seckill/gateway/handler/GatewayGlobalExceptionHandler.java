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
import com.alibaba.fastjson2.JSON;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */

// ... existing code ...
@Order(-1)
@Configuration
public class GatewayGlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status = determineStatus(ex);
        response.setStatusCode(status);

        Map<String, Object> errorResponse = buildErrorResponse(exchange, ex, status);

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

//        if (isRedisException(ex)) {
//            return HttpStatus.SERVICE_UNAVAILABLE;
//        }

        if (ex instanceof java.net.ConnectException ||
                isConnectionException(ex)) {
            return HttpStatus.BAD_GATEWAY;
        }

        if (ex instanceof java.util.concurrent.TimeoutException ||
                isTimeoutException(ex)) {
            return HttpStatus.GATEWAY_TIMEOUT;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String determineMessage(Throwable ex, HttpStatus status) {
        if (ex instanceof ResponseStatusException) {
            String reason = ((ResponseStatusException) ex).getReason();
            return reason != null ? reason : "请求处理失败";
        }

        if (ex instanceof NotFoundException) {
            return "服务未找到";
        }

//        if (isRedisException(ex)) {
//            return "缓存服务不可用";
//        }

        if (ex instanceof java.net.ConnectException ||
                isConnectionException(ex)) {
            return "后端服务连接失败";
        }

        if (ex instanceof java.util.concurrent.TimeoutException ||
                isTimeoutException(ex)) {
            return "请求超时";
        }

        return "系统内部错误";
    }

    private Map<String, Object> buildErrorResponse(
            ServerWebExchange exchange,
            Throwable ex,
            HttpStatus status) {

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", status.value());
        errorResponse.put("message", determineMessage(ex, status));
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("path", exchange.getRequest().getPath().toString());

        String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-Id");
        if (requestId != null && !requestId.isEmpty()) {
            errorResponse.put("requestId", requestId);
        }

        String stage = exchange.getRequest().getHeaders().getFirst("X-Stage");
        if (stage != null && !stage.isEmpty()) {
            errorResponse.put("stage", stage);
        }

        return errorResponse;
    }

    private boolean isRedisException(Throwable ex) {
        String className = ex.getClass().getName();
        return className.contains("RedisException") ||
                className.contains("RedisConnectionFailureException") ||
                className.contains("InvalidDataAccessApiUsageException");
    }

    private boolean isConnectionException(Throwable ex) {
        String className = ex.getClass().getName();
        return className.contains("ConnectException") ||
                className.contains("UnknownHostException") ||
                className.contains("NoAvailablePeersException");
    }

    private boolean isTimeoutException(Throwable ex) {
        String className = ex.getClass().getName();
        return className.contains("TimeoutException") ||
                className.contains("ReadTimeoutException") ||
                className.contains("ConnectTimeoutException");
    }
}