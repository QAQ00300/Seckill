package com.seckill.order.api.external;

import com.seckill.common.tools.result.Result;
import com.seckill.order.biz.service.OrderService;
import com.seckill.order.bo.eo.OrderEO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "订单管理", description = "订单相关API")
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    @Operation(summary = "创建订单", description = "创建新的订单")
    public Result<String> createOrder(@Valid @RequestBody OrderEO order) {
        try {
            String orderNo = orderService.createOrder(order);
            return Result.success(orderNo);
        } catch (Exception e) {
            log.error("创建订单失败: userId={}", order.getUserId(), e);
            return Result.error(500, "创建订单失败: " + e.getMessage());
        }
    }

    /**
     * 根据订单号查询订单
     */
    @GetMapping("/{orderNo}")
    @Operation(summary = "查询订单详情", description = "根据订单号查询订单详细信息")
    public Result<OrderEO> getOrderByNo(
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        try {
            OrderEO order = orderService.getOrderByNo(orderNo);
            if (order != null) {
                return Result.success(order);
            } else {
                return Result.error(404, "订单不存在");
            }
        } catch (Exception e) {
            log.error("查询订单失败: orderNo={}", orderNo, e);
            return Result.error(500, "查询订单失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID查询订单列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户订单", description = "根据用户ID查询订单列表")
    public Result<List<OrderEO>> getOrdersByUserId(
            @Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            List<OrderEO> orders = orderService.getOrdersByUserId(userId);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("查询用户订单失败: userId={}", userId, e);
            return Result.error(500, "查询用户订单失败: " + e.getMessage());
        }
    }

    /**
     * 根据商品ID查询订单列表
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "查询商品订单", description = "根据商品ID查询订单列表")
    public Result<List<OrderEO>> getOrdersByProductId(
            @Parameter(description = "商品ID") @PathVariable Integer productId) {
        try {
            List<OrderEO> orders = orderService.getOrdersByProductId(productId);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("查询商品订单失败: productId={}", productId, e);
            return Result.error(500, "查询商品订单失败: " + e.getMessage());
        }
    }

    /**
     * 根据订单状态查询订单列表
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "查询指定状态订单", description = "根据订单状态查询订单列表")
    public Result<List<OrderEO>> getOrdersByStatus(
            @Parameter(description = "订单状态") @PathVariable Integer status) {
        try {
            List<OrderEO> orders = orderService.getOrdersByStatus(status);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("查询指定状态订单失败: status={}", status, e);
            return Result.error(500, "查询指定状态订单失败: " + e.getMessage());
        }
    }

    /**
     * 根据秒杀活动ID查询订单列表
     */
    @GetMapping("/seckill/{seckillId}")
    @Operation(summary = "查询秒杀订单", description = "根据秒杀活动ID查询订单列表")
    public Result<List<OrderEO>> getOrdersBySeckillId(
            @Parameter(description = "秒杀活动ID") @PathVariable String seckillId) {
        try {
            List<OrderEO> orders = orderService.getOrdersBySeckillId(seckillId);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("查询秒杀订单失败: seckillId={}", seckillId, e);
            return Result.error(500, "查询秒杀订单失败: " + e.getMessage());
        }
    }

    /**
     * 更新订单信息
     */
    @PutMapping
    @Operation(summary = "更新订单", description = "更新订单信息")
    public Result<Boolean> updateOrder(@Valid @RequestBody OrderEO order) {
        try {
            boolean result = orderService.updateOrder(order);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新订单失败: orderNo={}", order.getOrderNo(), e);
            return Result.error(500, "更新订单失败: " + e.getMessage());
        }
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{orderNo}/status")
    @Operation(summary = "更新订单状态", description = "更新订单状态")
    public Result<Boolean> updateOrderStatus(
            @Parameter(description = "订单号") @PathVariable String orderNo,
            @Parameter(description = "新状态") @RequestParam Integer status) {
        try {
            boolean result = orderService.updateOrderStatus(orderNo, status);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新订单状态失败: orderNo={}, status={}", orderNo, status, e);
            return Result.error(500, "更新订单状态失败: " + e.getMessage());
        }
    }

    /**
     * 删除订单（逻辑删除）
     */
    @DeleteMapping("/{orderNo}")
    @Operation(summary = "删除订单", description = "逻辑删除订单")
    public Result<Boolean> deleteOrder(
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        try {
            boolean result = orderService.deleteOrder(orderNo);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除订单失败: orderNo={}", orderNo, e);
            return Result.error(500, "删除订单失败: " + e.getMessage());
        }
    }

    /**
     * 统计用户订单数量
     */
    @GetMapping("/count/user/{userId}")
    @Operation(summary = "统计用户订单数", description = "统计用户订单总数")
    public Result<Integer> countOrdersByUserId(
            @Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            int count = orderService.countOrdersByUserId(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户订单数失败: userId={}", userId, e);
            return Result.error(500, "统计用户订单数失败: " + e.getMessage());
        }
    }

    /**
     * 统计用户今日订单数量
     */
    @GetMapping("/count/today/{userId}")
    @Operation(summary = "统计用户今日订单数", description = "统计用户今日订单数量")
    public Result<Integer> countTodayOrdersByUserId(
            @Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            int count = orderService.countTodayOrdersByUserId(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户今日订单数失败: userId={}", userId, e);
            return Result.error(500, "统计用户今日订单数失败: " + e.getMessage());
        }
    }

    /**
     * 统计用户指定状态的订单数量
     */
    @GetMapping("/count/user/{userId}/status/{status}")
    @Operation(summary = "统计用户指定状态订单数", description = "统计用户指定状态的订单数量")
    public Result<Integer> countOrdersByUserIdAndStatus(
            @Parameter(description = "用户ID") @PathVariable Integer userId,
            @Parameter(description = "订单状态") @PathVariable Integer status) {
        try {
            int count = orderService.countOrdersByUserIdAndStatus(userId, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户指定状态订单数失败: userId={}, status={}", userId, status, e);
            return Result.error(500, "统计用户指定状态订单数失败: " + e.getMessage());
        }
    }
}