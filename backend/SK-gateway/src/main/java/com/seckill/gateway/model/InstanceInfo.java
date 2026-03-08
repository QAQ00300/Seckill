package com.seckill.gateway.model;

import lombok.Data;

/**
 * 服务实例信息
 */
@Data
public class InstanceInfo {

    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 权重（用于负载均衡）
     */
    private Integer weight = 1;

    /**
     * 是否健康
     */
    private Boolean healthy = true;
}