package com.seckill.backend.controller;

import com.seckill.backend.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RateLimitService rateLimitService;

    /**
     * 秒杀
     * @param productId
     * @param userId
     * @return
     */
    @PostMapping("/{producttId}")
    public ResponseEntity<SeckillResponse> seckill(
            @PathVariable Long productId
            @RequestHeader("userId") Long userId) {

        //限流检查
        if (!rateLimitService.tryAcquire("seckill:" + productId, 1000, 100)) {
            return ResponseEntity.status(429).body(SeckillResponse.fail("请求过于频繁"));
        }

        //执行秒杀
        boolean success = seckillService.executeSeckill(productId, userId);

        if(success){
            return ResponseEntity.ok(SeckillResponse.success("秒杀成功"));
        }else {
            return ResponseEntity.ok(SeckillResponse.fail("秒杀失败"));
        }

    }

    /**
     * 获取秒杀结果
     * @param productId
     * @param userId
     * @return
     */
    @GetMapping("/result/{productId}")
    public SeckillResult getResult(@PathVariable Long productId
                                   @RequestHeader("userId") Long userId) {
        return seckillService.getSeckillResult(productId,userId);
    }

    @PostMapping("/token")
    public String getSeckillToken(@RequestHeader("userId") Long userId,
                                  @RequestParam Long productId){
        return seckillService.generateToken(userId, productId);
    }
}

