package com.seckill.core.seckill.client;


import com.seckill.common.tools.result.Result;

import com.seckill.core.seckill.dto.SeckillOrderDTO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "sk-order")
public interface SeckillOrderClient {
    /**
     * 检查用户是否已参与秒杀
     */
    @GetMapping("/api/order/seckill/query/check-participation")
    Result<Boolean> canParticipate(
            @RequestParam("userId") Long userId,
            @RequestParam("seckillId") Long seckillId
    );

    /**
     * 创建秒杀订单
     */
    @PostMapping("/api/order/seckill/mutation/create")
    Result<String> createOrder(
            @RequestBody SeckillOrderDTO orderDTO
    );

}

