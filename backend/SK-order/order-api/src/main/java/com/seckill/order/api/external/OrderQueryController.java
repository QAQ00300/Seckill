package com.seckill.order.api.external;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.tools.result.Result;
import com.seckill.order.biz.service.OrderService;
import com.seckill.order.bo.eo.OrderEO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/order/query")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "订单查询接口", description = "普通订单查询相关 API（GET）")
public class OrderQueryController {

    private final OrderService orderService;

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
            log.error("查询订单失败：orderNo={}", orderNo, e);
            return Result.error(500, "查询订单失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户订单列表", description = "根据用户 ID 查询订单列表")
    public Result<List<OrderEO>> getOrdersByUserId(
            @Parameter(description = "用户 ID") @PathVariable Long userId) {
        try {
            List<OrderEO> orders = orderService.getOrdersByUserId(userId);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("查询用户订单失败：userId={}", userId, e);
            return Result.error(500, "查询用户订单失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/page")
    @Operation(summary = "分页查询用户订单", description = "分页查询用户订单列表")
    public Result<Page<OrderEO>> pageOrdersByUserId(
            @Parameter(description = "用户 ID") @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<OrderEO> page = orderService.pageOrdersByUserId(userId, pageNum, pageSize);
            return Result.success(page);
        } catch (Exception e) {
            log.error("分页查询用户订单失败：userId={}", userId, e);
            return Result.error(500, "分页查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "查询商品订单列表", description = "根据商品 ID 查询订单列表")
    public Result<List<OrderEO>> getOrdersByProductId(
            @Parameter(description = "商品 ID") @PathVariable Long productId) {
        try {
            List<OrderEO> orders = orderService.getOrdersByProductId(productId);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("查询商品订单失败：productId={}", productId, e);
            return Result.error(500, "查询商品订单失败：" + e.getMessage());
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询订单", description = "根据订单状态查询订单列表")
    public Result<List<OrderEO>> getOrdersByStatus(
            @Parameter(description = "订单状态") @PathVariable Integer status) {
        try {
            List<OrderEO> orders = orderService.getOrdersByStatus(status);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("按状态查询订单失败：status={}", status, e);
            return Result.error(500, "按状态查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "统计用户订单数量", description = "统计用户的订单总数")
    public Result<Integer> countOrdersByUserId(
            @Parameter(description = "用户 ID") @PathVariable Long userId) {
        try {
            int count = orderService.countOrdersByUserId(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户订单失败：userId={}", userId, e);
            return Result.error(500, "统计订单失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/today-count")
    @Operation(summary = "统计用户今日订单数", description = "统计用户今天的订单数量")
    public Result<Integer> countTodayOrdersByUserId(
            @Parameter(description = "用户 ID") @PathVariable Long userId) {
        try {
            int count = orderService.countTodayOrdersByUserId(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户今日订单失败：userId={}", userId, e);
            return Result.error(500, "统计今日订单失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/status/{status}/count")
    @Operation(summary = "统计用户指定状态订单数", description = "统计用户指定状态的订单数量")
    public Result<Integer> countOrdersByUserIdAndStatus(
            @Parameter(description = "用户 ID") @PathVariable Long userId,
            @Parameter(description = "订单状态") @PathVariable Integer status) {
        try {
            int count = orderService.countOrdersByUserIdAndStatus(userId, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户指定状态订单失败：userId={}, status={}", userId, status, e);
            return Result.error(500, "统计订单失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/total-amount")
    @Operation(summary = "计算用户订单总金额", description = "计算用户所有订单的总金额")
    public Result<BigDecimal> calculateTotalAmountByUserId(
            @Parameter(description = "用户 ID") @PathVariable Long userId) {
        try {
            BigDecimal total = orderService.calculateTotalAmountByUserId(userId);
            return Result.success(total);
        } catch (Exception e) {
            log.error("计算用户订单总金额失败：userId={}", userId, e);
            return Result.error(500, "计算总金额失败：" + e.getMessage());
        }
    }
}