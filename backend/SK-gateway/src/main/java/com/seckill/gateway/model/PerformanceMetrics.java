package com.seckill.gateway.model;



import lombok.Data;
import lombok.Builder;


/**
 * 性能指标模型
 */
@Data
@Builder
public class PerformanceMetrics {
    private String requestId;
    private String stage;
    private String api;
    private String method;
    private int status;
    private long duration;
    private long timestamp;
    private boolean success;
    private String clientIp;
    private String userId;
    private String errorMsg;
}

