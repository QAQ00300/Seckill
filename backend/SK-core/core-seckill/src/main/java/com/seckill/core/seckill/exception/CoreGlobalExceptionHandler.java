package com.seckill.core.seckill.exception;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class CoreGlobalExceptionHandler {

    /**
     * 通用响应格式
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Result<T> {
        private Integer code;
        private String message;
        private T data;

        public static <T> Result<T> success(T data) {
            return new Result<>(200, "success", data);
        }

        public static <T> Result<T> error(Integer code, String message) {
            return new Result<>(code, message, null);
        }

        public static <T> Result<T> error(Integer code, String message, T data) {
            return new Result<>(code, message, data);
        }
    }

    /**
     * 业务异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<Map<String, Object>> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常 - URI: {}, Method: {}, Error: {}",
                request.getRequestURI(), request.getMethod(), e.getMessage(), e);

        Map<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("timestamp", System.currentTimeMillis());
        errorInfo.put("path", request.getRequestURI());
        errorInfo.put("method", request.getMethod());
        errorInfo.put("error", e.getMessage());

        return Result.error(500, "系统异常，请稍后重试", errorInfo);
    }

    /**
     * 秒杀业务异常
     */
    @ExceptionHandler(SeckillException.class)
    public Result<String> handleSeckillException(SeckillException e) {
        log.warn("秒杀业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }
}

