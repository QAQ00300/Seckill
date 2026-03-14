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

@RestController
@RequestMapping("/api/order/mutation")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "订单变更接口", description = "普通订单增删改相关 API（POST/PUT/DELETE）")
public class OrderMutationController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "创建普通订单", description = "创建新的普通订单")
    public Result<String> createOrder(@Valid @RequestBody OrderEO order) {
        try {
            String orderNo = orderService.createOrder(order);
            return Result.success(orderNo);
        } catch (Exception e) {
            log.error("创建普通订单失败：userId={}", order.getUserId(), e);
            return Result.error(500, "创建订单失败：" + e.getMessage());
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
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        try {
            boolean result = orderService.deleteOrder(orderNo);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除订单失败：orderNo={}", orderNo, e);
            return Result.error(500, "删除订单失败：" + e.getMessage());
        }
    }

    @PutMapping("/{orderNo}")
    @Operation(summary = "更新订单", description = "更新订单信息")
    public Result<Boolean> updateOrder(
            @Parameter(description = "订单号") @PathVariable String orderNo,
            @Valid @RequestBody OrderEO order) {
        try {
            order.setOrderNo(Integer.valueOf(orderNo));
            boolean result = orderService.updateOrder(order);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新订单失败：orderNo={}", orderNo, e);
            return Result.error(500, "更新订单失败：" + e.getMessage());
        }
    }

    @PutMapping("/{orderNo}/status")
    @Operation(summary = "更新订单状态", description = "更新订单状态")
    public Result<Boolean> updateOrderStatus(
            @Parameter(description = "订单号") @PathVariable String orderNo,
            @Parameter(description = "新状态") @RequestParam Integer status) {
        try {
            boolean result = orderService.updateOrderStatus(orderNo, status);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新订单状态失败：orderNo={}, status={}", orderNo, status, e);
            return Result.error(500, "更新订单状态失败：" + e.getMessage());
        }
    }
}