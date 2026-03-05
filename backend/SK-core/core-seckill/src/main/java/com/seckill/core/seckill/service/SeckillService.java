package com.seckill.core.seckill.service;



import com.seckill.core.seckill.dto.SeckillRequest;
import com.seckill.core.seckill.dto.SeckillResponse;
import com.seckill.core.seckill.model.SeckillActivity;

/**
 * 秒杀服务接口
 */
public interface SeckillService {

    /**
     * 处理秒杀请求（同步版）
     * 阶段1：直接同步处理
     */
    SeckillResponse processSeckill(SeckillRequest request);

    /**
     * 获取秒杀活动详情
     */
    SeckillActivity getSeckillActivity(Long seckillId);

    /**
     * 检查秒杀活动状态
     */
    boolean checkSeckillStatus(Long seckillId);

    /**
     * 获取秒杀库存
     */
    Integer getSeckillStock(Long seckillId);

    /**
     * 验证用户秒杀资格
     */
    boolean validateUserSeckill(Long userId, Long seckillId);
}