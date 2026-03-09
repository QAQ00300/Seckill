package com.seckill.core.boot.controller;


import com.seckill.core.boot.result.Result;
import com.seckill.core.seckill.dto.SeckillRequest;
import com.seckill.core.seckill.dto.SeckillResponse;
import com.seckill.core.seckill.model.SeckillActivity;
import com.seckill.core.seckill.service.SeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀接口控制器（用于压力测试）
 */
@RestController
@RequestMapping("/api/v1/seckill")
@RequiredArgsConstructor
@Api(tags = "秒杀接口")
public class SeckillController {

    private final SeckillService seckillService;

    @PostMapping("/execute")
    @ApiOperation("执行秒杀（压测核心接口）")
    public Result<SeckillResponse> executeSeckill(@RequestBody SeckillRequest request) {
        SeckillResponse response = seckillService.processSeckill(request);
        return Result.success(response);
    }

    @GetMapping("/detail/{seckillId}")
    @ApiOperation("获取秒杀活动详情")
    public Result<SeckillActivity> getDetail(@PathVariable Long seckillId) {
        SeckillActivity activity = seckillService.getSeckillActivity(seckillId);
        return activity != null ? Result.success(activity) : Result.fail("活动不存在");
    }

    @GetMapping("/status/{seckillId}")
    @ApiOperation("检查秒杀状态")
    public Result<Boolean> checkStatus(@PathVariable Long seckillId) {
        boolean status = seckillService.checkSeckillStatus(seckillId);
        return Result.success(status);
    }

    @GetMapping("/stock/{seckillId}")
    @ApiOperation("查询剩余库存")
    public Result<Integer> getStock(@PathVariable Long seckillId) {
        Integer stock = seckillService.getSeckillStock(seckillId);
        return Result.success(stock);
    }
}