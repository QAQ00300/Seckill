package com.seckill.order.api.external;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
@RequestMapping("/api/order/normal")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "普通订单管理", description = "普通订单相关 API")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "创建普通订单", description= "创建新的普通订单")
    public Result<String> createOrder(@Valid @RequestBody OrderEO order) {
        try {
            String orderNo = orderService.createOrder(order);
            return Result.success(orderNo);
        } catch (Exception e) {
            log.error("创建普通订单失败：userId={}", order.getUserId(), e);
            return Result.error(500, "创建订单失败：" + e.getMessage());
        }
    }

    @GetMapping("/{orderNo}")
    @Operation(summary = "查询订单详情", description = "根据订单号查询订单详细信息")
    public Result<OrderEO> getOrderByNo(
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        try {
            OrderEO order= orderService.getOrderByNo(orderNo);
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
    @Operation(summary = "分页查询用户订单", description= "分页查询用户订单列表")
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

    @PutMapping("/{orderNo}/cancel")
    @Operation(summary = "取消订单", description = "取消待处理的订单")
    public Result<Boolean> cancelOrder(
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        try {
            boolean result = orderService.cancelOrder(orderNo);
            return Result.success(result);
        } catch (Exception e) {
            log.error("取消订单失败：orderNo={}", orderNo, e);
            return Result.error(500, "取消订单失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{orderNo}")
    @Operation(summary = "删除订单", description = "删除订单记录")
    public Result<Boolean> deleteOrder(
            @Parameter(description= "订单号") @PathVariable String orderNo) {
        try {
            boolean result = orderService.deleteOrder(orderNo);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除订单失败：orderNo={}", orderNo, e);
            return Result.error(500, "删除订单失败：" + e.getMessage());
        }
    }
}