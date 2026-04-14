package com.seckill.core.seckill.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seckill.core.cache.service.CacheService;
import com.seckill.core.cache.DistributedLockService;
import com.seckill.core.seckill.client.SeckillOrderClient;
import com.seckill.core.seckill.dto.SeckillOrderDTO;
import com.seckill.core.seckill.dto.SeckillRequest;
import com.seckill.core.seckill.dto.SeckillResponse;
import com.seckill.core.seckill.exception.SeckillException;
import com.seckill.core.seckill.mapper.SeckillActivityMapper;
import com.seckill.core.seckill.model.SeckillActivity;
import com.seckill.core.seckill.service.SeckillService;
import com.seckill.core.seckill.validator.SeckillValidator;
import com.seckill.core.stock.service.StockService;
import com.seckill.common.tools.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀服务实现类（阶段 1：同步处理）
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SeckillServiceImpl implements SeckillService {

    private final SeckillActivityMapper seckillActivityMapper;
    private final StockService stockService;
    private final SeckillOrderClient seckillOrderClient;
    private final SeckillValidator seckillValidator;
    private final CacheService cacheService;
    private final DistributedLockService distributedLockService;

    private static final String SECKILL_ACTIVITY_KEY_PREFIX = "seckill:activity:";
    private static final long ACTIVITY_CACHE_EXPIRE_SECONDS = 300;


    @Override
    public SeckillResponse processSeckill(SeckillRequest request) {
        log.info("开始处理秒杀请求 - userId: {}, seckillId: {}",
                request.getUserId(), request.getSeckillId());

        long startTime = System.currentTimeMillis();

        // 生成分布式锁key
        String lockKey = "seckill:lock:" + request.getSeckillId();

        try {
            // 1. 基础请求验证
            seckillValidator.validateRequest(request);

            // 2. 验证秒杀活动（在事务内）
            SeckillActivity activity = validateSeckill(request.getSeckillId());

            // 3. 验证用户资格（Feign 远程调用，不在事务内）
            Result<Boolean> canParticipateResult = seckillOrderClient.canParticipate(
                    request.getUserId(),
                    request.getSeckillId()
            );

            if (!canParticipateResult.isSuccess() || Boolean.FALSE.equals(canParticipateResult.getData())) {
                throw new SeckillException(400, "您已参加过该秒杀活动");
            }

            // 4. 获取分布式锁
            boolean locked = distributedLockService.tryLockWithRetry(lockKey, 10, 3, 50);
            if (!locked) {
                throw new SeckillException(400, "系统繁忙，请稍后重试");
            }

            try {
                // 5-6. 扣减库存并创建订单（事务内）
                return processDeductAndCreateOrder(request, activity, startTime);
            } finally {
                // 释放分布式锁
                distributedLockService.unlock(lockKey);
            }

        } catch (SeckillException e) {
            log.warn("秒杀失败：{}", e.getMessage());
            return SeckillResponse.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("系统异常", e);
            // 确保释放锁
            distributedLockService.unlock(lockKey);
            return SeckillResponse.fail(500, "系统繁忙，请稍后重试");
        }
    }

    /**
     * 处理库存扣减和订单创建（带事务）
     */
    @Transactional(rollbackFor = Exception.class)
    public SeckillResponse processDeductAndCreateOrder(SeckillRequest request,
                                                       SeckillActivity activity,
                                                       long startTime) {
        try {
            // 4. 扣减库存（同步）
            boolean deducted = stockService.deductStock(
                    request.getSeckillId(),
                    request.getQuantity()
            );

            if (!deducted) {
                throw new SeckillException(400, "库存不足");
            }

            // 5. 创建订单（通过 Feign 远程调用 order 服务）
            SeckillOrderDTO orderDTO = buildSeckillOrderDTO(request, activity);
            Result<String> orderResult = seckillOrderClient.createOrder(orderDTO);

            if (!orderResult.isSuccess()) {
                // 阶段 1：记录补偿日志，后续需要回滚库存
                log.error("订单创建失败，需要补偿回滚库存 - seckillId: {}, userId: {}",
                        request.getSeckillId(), request.getUserId());
                // TODO: 阶段 3 使用 MQ 事务消息保证最终一致性
                throw new SeckillException(400, "订单创建失败：" + orderResult.getMessage());
            }


            //订单创建成功后删除缓存
            String orderNo = orderResult.getData();
            invalidateSeckillActivityCache(request.getSeckillId());  // 删除缓存

            long duration = System.currentTimeMillis() - startTime;
            log.info("秒杀成功 - orderId: {}, duration: {}ms", orderNo, duration);

            return SeckillResponse.success(orderNo, activity.getSeckillPrice());

        } catch (SeckillException e) {
            throw e;
        } catch (Exception e) {
            log.error("处理过程异常", e);
            throw new SeckillException(500, "系统错误");
        }
    }

    @Override
    public SeckillActivity getSeckillActivity(Long seckillId) {
        // 1. 先尝试从缓存获取
        String cacheKey = SECKILL_ACTIVITY_KEY_PREFIX + seckillId;
        SeckillActivity cached = cacheService.get(cacheKey, SeckillActivity.class);
        if (cached != null) {
            log.debug("缓存命中 - seckillId: {}", seckillId);
            return cached;
        }

        // 2. 缓存未命中，查询数据库
        log.debug("缓存未命中，查询数据库 - seckillId: {}", seckillId);
        SeckillActivity activity = seckillActivityMapper.selectById(seckillId);

        // 3. 写入缓存
        if (activity != null) {
            cacheService.set(cacheKey, activity, ACTIVITY_CACHE_EXPIRE_SECONDS);
            log.debug("回写缓存 - seckillId: {}", seckillId);
        }

        return activity;
    }

    @Override
    public boolean checkSeckillStatus(Long seckillId) {
        SeckillActivity activity = getSeckillActivity(seckillId);
        if (activity == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        return activity.getStatus() == 1
                && now.isAfter(activity.getStartTime())
                && now.isBefore(activity.getEndTime());
    }

    @Override
    public Integer getSeckillStock(Long seckillId) {
        String cacheKey = "seckill:stock:" + seckillId;
        // 尝试从缓存获取
        Integer stock = cacheService.get(cacheKey, Integer.class);
        if (stock != null) {
            return stock;
        }
        // 缓存未命中，从数据库查询
        SeckillActivity activity = getSeckillActivity(seckillId);
        stock = activity != null ? activity.getRemainStock() : 0;
        // 存入缓存，设置过期时间为30秒（库存变化频繁）
        cacheService.set(cacheKey, stock, 30);
        return stock;
    }

    @Override
    public boolean validateUserSeckill(Long userId, Long seckillId) {
        return false;
    }


    /**
     * 验证秒杀活动（优先从缓存获取）
     */
    private SeckillActivity validateSeckill(Long seckillId) {
        SeckillActivity activity = getSeckillActivity(seckillId);
        if (activity == null) {
            throw new SeckillException(404, "秒杀活动不存在");
        }

        if (activity.getStatus() != 1) {
            throw new SeckillException(400, "秒杀活动已结束");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartTime())) {
            throw new SeckillException(400, "秒杀尚未开始");
        }
        if (now.isAfter(activity.getEndTime())) {
            throw new SeckillException(400, "秒杀活动已结束");
        }

        return activity;
    }

    /**
     * 使缓存失效（用于数据更新后）
     */
    private void invalidateSeckillActivityCache(Long seckillId) {
        String cacheKey = SECKILL_ACTIVITY_KEY_PREFIX + seckillId;
        cacheService.delete(cacheKey);
        log.debug("缓存已失效 - seckillId: {}", seckillId);
    }

    private SeckillOrderDTO buildSeckillOrderDTO(SeckillRequest request, SeckillActivity activity) {
        BigDecimal totalPrice = activity.getSeckillPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        return SeckillOrderDTO.builder()
                .userId(request.getUserId())
                .seckillId(request.getSeckillId())
                .productId(activity.getProductId())
                .unitPrice(activity.getSeckillPrice())
                .seckillPrice(activity.getSeckillPrice())
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .orderStatus(0) // 待处理
                .build();
    }


}