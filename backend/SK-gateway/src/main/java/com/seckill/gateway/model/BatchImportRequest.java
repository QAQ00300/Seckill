package com.seckill.gateway.model;

import lombok.Data;
import java.util.List;

/**
 * 批量导入请求
 */
@Data
public class BatchImportRequest {

    /**
     * 路由列表
     */
    private List<RouteRegisterRequest> routes;
}
