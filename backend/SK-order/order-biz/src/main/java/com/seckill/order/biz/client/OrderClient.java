package com.seckill.order.biz.client;

import com.seckill.common.tools.result.Result;
import com.seckill.order.bo.eo.OrderEO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 订单服务 Feign 客户端接口
 * 用于 user 服务调用 order 服务的能力
 *
 * 使用场景示例：
 * - 查询用户信息时展示订单统计
 * - 用户详情页展示订单数量
 */
@FeignClient(name = "sk-order", contextId = "userOrderClient")
public interface OrderClient {

    /**
     * 查询用户订单列表
     * @param userId 用户 ID
     * @return 订单列表
     */
    @GetMapping("/api/order/normal/user/{userId}")
    Result<List<OrderEO>> getOrdersByUserId(@PathVariable("userId") Long userId);

    /**
     * 统计用户订单数量
     * @param userId 用户 ID
     * @return 订单数量
     */
    @GetMapping("/api/order/normal/user/{userId}/count")
    Result<Integer> countOrdersByUserId(@PathVariable("userId") Long userId);
}