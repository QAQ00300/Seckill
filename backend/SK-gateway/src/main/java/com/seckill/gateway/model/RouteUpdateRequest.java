package com.seckill.gateway.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 路由更新请求
 */
@Data
public class RouteUpdateRequest {

    /**
     * 服务 ID
     */
    private String serviceId;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 上下文路径
     */
    private String contextPath;

    /**
     * 服务实例列表
     */
    private List<InstanceInfo> instances;

    /**
     * 元数据信息
     */
    private Map<String, String> metadata;
}