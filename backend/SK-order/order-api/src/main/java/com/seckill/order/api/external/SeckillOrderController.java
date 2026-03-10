package com.seckill.order.api.external;

import com.seckill.common.tools.result.Result;
import com.seckill.order.biz.service.SeckillOrderService;
import com.seckill.order.bo.eo.SeckillOrderEO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order/seckill")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "秒杀订单管理", description = "秒杀订单相关 API")
public class SeckillOrderController {

    private final SeckillOrderService seckillOrderService;

    @GetMapping("/{orderNo}")
    @Operation(summary = "查询秒杀订单详情", description = "根据订单号查询秒杀订单详细信息")
    public Result<SeckillOrderEO> getOrderByNo(
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        try {
            SeckillOrderEO order = seckillOrderService.getOrderByNo(orderNo);
            if (order != null) {
                return Result.success(order);
            } else {
                return Result.error(404, "订单不存在");
            }
        } catch (Exception e) {
            log.error("查询秒杀订单失败：orderNo={}", orderNo, e);
            return Result.error(500, "查询订单失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户秒杀订单", description = "根据用户 ID 查询秒杀订单列表")
    public Result<List<SeckillOrderEO>> getOrdersByUserId(
            @Parameter(description= "用户 ID") @PathVariable Long userId) {
        try {
            List<SeckillOrderEO> orders = seckillOrderService.getOrdersByUserId(userId);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("查询用户秒杀订单失败：userId={}", userId, e);
            return Result.error(500, "查询订单失败：" + e.getMessage());
        }
    }

    @GetMapping("/seckill/{seckillId}")
    @Operation(summary = "查询秒杀活动订单", description = "根据秒杀活动 ID 查询订单列表")
    public Result<List<SeckillOrderEO>> getOrdersBySeckillId(
            @Parameter(description= "秒杀活动 ID") @PathVariable Long seckillId) {
        try {
            List<SeckillOrderEO> orders = seckillOrderService.getOrdersBySeckillId(seckillId);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("查询秒杀活动订单失败：seckillId={}", seckillId, e);
            return Result.error(500, "查询订单失败：" + e.getMessage());
        }
    }

    @PutMapping("/{orderNo}/cancel")
    @Operation(summary = "取消秒杀订单", description= "取消待处理的秒杀订单")
    public Result<Boolean> cancelOrder(
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        try {
            boolean result = seckillOrderService.cancelOrder(orderNo);
            return Result.success(result);
        } catch (Exception e) {
            log.error("取消秒杀订单失败：orderNo={}", orderNo, e);
            return Result.error(500, "取消订单失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/seckill/{seckillId}/participated")
    @Operation(summary = "检查用户是否已参与秒杀", description= "检查用户是否已经参加过该秒杀活动")
    public Result<Boolean> hasParticipated(
            @Parameter(description = "用户 ID") @PathVariable Long userId,
            @Parameter(description = "秒杀活动 ID") @PathVariable Long seckillId) {
        try {
            boolean participated = seckillOrderService.hasParticipated(userId, seckillId);
            return Result.success(participated);
        } catch (Exception e) {
            log.error("检查用户参与状态失败：userId={}, seckillId={}", userId, seckillId, e);
            return Result.error(500, "检查失败：" + e.getMessage());
        }
    }
}