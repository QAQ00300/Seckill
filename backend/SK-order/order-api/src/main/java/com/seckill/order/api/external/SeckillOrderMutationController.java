package com.seckill.order.api.external;

import com.seckill.common.tools.result.Result;
import com.seckill.order.biz.service.SeckillOrderService;
import com.seckill.order.bo.eo.SeckillOrderEO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order/seckill/mutation")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "秒杀订单变更接口", description = "秒杀订单增删改相关 API（POST/PUT/DELETE）")
public class SeckillOrderMutationController {

    private final SeckillOrderService seckillOrderService;

    @PostMapping("/create")
    @Operation(summary = "创建秒杀订单", description = "创建秒杀订单")
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