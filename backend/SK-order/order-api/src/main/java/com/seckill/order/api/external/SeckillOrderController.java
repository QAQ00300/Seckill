package com.seckill.order.api.external;

import com.seckill.common.tools.result.Result;
import com.seckill.order.biz.service.SeckillOrderService;
import com.seckill.order.bo.eo.SeckillOrderEO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/order/seckill")
@Slf4j
@RequiredArgsConstructor
public class SeckillOrderController {

    private final SeckillOrderService seckillOrderService;

    /**
     * 检查用户是否已参与秒杀（供 Feign 调用）
     */
    @GetMapping("/check-participation")
    public Result<Boolean> checkParticipation(
            @RequestParam Long userId,
            @RequestParam Long seckillId) {

        boolean participated = seckillOrderService.hasParticipated(userId, seckillId);
        return Result.success(participated);
    }

    /**
     * 创建秒杀订单（供 Feign 调用）
     * 注意：这里需要接收 DTO 参数，但为了简化，我们直接使用 EO
     */
    @PostMapping("/create")
    public Result<String> createOrder(@RequestBody SeckillOrderEO order) {
        try {
            String orderNo = seckillOrderService.createSeckillOrder(order);
            return Result.success(orderNo);
        } catch (Exception e) {
            log.error("创建秒杀订单失败", e);
            return Result.error(500, "订单创建失败：" + e.getMessage());
        }
    }
}