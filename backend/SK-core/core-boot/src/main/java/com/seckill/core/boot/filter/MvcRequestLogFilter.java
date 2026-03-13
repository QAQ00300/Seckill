package com.seckill.core.boot.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 请求日志过滤器
 */
@Component
@Order(1)
@Slf4j
public class MvcRequestLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            // 记录请求开始
            logRequestStart(request);

            filterChain.doFilter(request, response);

        } finally {
            // 记录请求结束
            long duration = System.currentTimeMillis() - startTime;
            logRequestEnd(request, response, duration);
        }
    }

    private void logRequestStart(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== 请求开始 ==========\n");
        sb.append("URL: ").append(request.getRequestURL()).append("\n");
        sb.append("Method: ").append(request.getMethod()).append("\n");
        sb.append("IP: ").append(request.getRemoteAddr()).append("\n");
        sb.append("=================================");
        log.info(sb.toString());
    }

    private void logRequestEnd(HttpServletRequest request,
                               HttpServletResponse response,
                               long duration) {

        StringBuilder sb = new StringBuilder();
        sb.append("\n========== 请求结束 ==========\n");
        sb.append("URL: ").append(request.getRequestURL()).append("\n");
        sb.append("Method: ").append(request.getMethod()).append("\n");
        sb.append("Status: ").append(response.getStatus()).append("\n");
        sb.append("Duration: ").append(duration).append("ms\n");
        sb.append("=================================");

        // 根据耗时使用不同日志级别
        if (duration > 1000) {
            log.warn(sb.toString());  // 超过1秒，警告级别
        } else {
            log.info(sb.toString());
        }
    }
}
