package com.seckill.core.boot.controller;


import com.seckill.core.boot.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀接口控制器（用于压力测试）
 */
@RestController
@RequestMapping("/api/v1/seckill")
@Api(tags = "秒杀接口")
public class SeckillController {

    @PostMapping("/execute")
    @ApiOperation("执行秒杀（压测核心接口）")
    public Result<String> executeSeckill(@RequestBody Object request) {
        return Result.success("Seckill service is not available in this stage");
    }

    @GetMapping("/detail/{seckillId}")
    @ApiOperation("获取秒杀活动详情")
    public Result<String> getDetail(@PathVariable Long seckillId) {
        return Result.success("Seckill service is not available in this stage");
    }

    @GetMapping("/status/{seckillId}")
    @ApiOperation("检查秒杀状态")
    public Result<Boolean> checkStatus(@PathVariable Long seckillId) {
        return Result.success(false);
    }

    @GetMapping("/stock/{seckillId}")
    @ApiOperation("查询剩余库存")
    public Result<Integer> getStock(@PathVariable Long seckillId) {
        return Result.success(0);
    }
}