package com.seckill.order.api.external;

import com.seckill.common.tools.result.Result;
import com.seckill.order.biz.service.SeckillOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order/seckill/query")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "秒杀订单查询接口", description = "秒杀订单查询相关 API（GET）")
public class SeckillOrderQueryController {

    private final SeckillOrderService seckillOrderService;

    @GetMapping("/check-participation")
    @Operation(summary = "检查用户是否可参与秒杀", description = "返回 true 表示可参与，false 表示已参与过")
    public Result<Boolean> checkParticipation(
            @Parameter(description = "用户 ID") @RequestParam Long userId,
            @Parameter(description = "秒杀 ID") @RequestParam Long seckillId) {
        try {
            // 查询是否已参与
            boolean participated = seckillOrderService.hasParticipated(userId, seckillId);
            // 返回相反值：未参与过=true(可参与)，已参与过=false(不可参与)
            return Result.success(!participated);
        } catch (Exception e) {
            log.error("检查用户参与状态失败：userId={}, seckillId={}", userId, seckillId, e);
            return Result.error(500, "检查失败：" + e.getMessage());
        }
    }
}