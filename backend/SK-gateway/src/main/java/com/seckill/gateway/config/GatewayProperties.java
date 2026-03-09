package com.seckill.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 网关自定义配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "sk.gateway")
public class GatewayProperties {

    /**
     * 阶段配置
     */
    private String stage = "stage1";

    /**
     * 限流配置
     */
    private RateLimit rateLimit = new RateLimit();

    /**
     * 请求日志配置
     */
    private RequestLog requestLog = new RequestLog();


    @Data
    public static class RateLimit {
        private boolean enabled;
        private int defaultLimit;
        private int defaultWindow;
    }

    @Data
    public static class RequestLog {
        private boolean enabled = true;
        private int maxBodyLength = 1000;
        private List<String> includeMethods = Arrays.asList("POST", "PUT", "PATCH");
        private List<String> excludePaths = Arrays.asList("/actuator", "/swagger", "/api-docs", "/favicon.ico", "/gateway");
    }


}