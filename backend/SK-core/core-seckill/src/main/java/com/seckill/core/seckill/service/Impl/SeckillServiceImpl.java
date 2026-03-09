package com.seckill.core.seckill.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seckill.core.seckill.dto.SeckillRequest;
import com.seckill.core.seckill.dto.SeckillResponse;
import com.seckill.core.seckill.exception.SeckillException;
import com.seckill.core.seckill.mapper.SeckillActivityMapper;
import com.seckill.core.seckill.mapper.SeckillOrderMapper;
import com.seckill.core.seckill.model.SeckillActivity;
import com.seckill.core.seckill.model.SeckillOrder;
import com.seckill.core.seckill.service.SeckillService;
import com.seckill.core.stock.service.StockService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 秒杀服务实现类（阶段 1：同步处理）
 */
@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class SeckillServiceImpl implements SeckillService {

    private final SeckillActivityMapper seckillActivityMapper;
    private final SeckillOrderMapper seckillOrderMapper;
    private final StockService stockService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeckillResponse processSeckill(SeckillRequest request) {
        log.info("开始处理秒杀请求 - userId: {}, seckillId: {}",
                request.getUserId(), request.getSeckillId());

        long startTime = System.currentTimeMillis();

        try {
            // 1. 验证秒杀活动
            SeckillActivity activity = validateSeckill(request.getSeckillId());

            // 2. 验证用户资格
            if (!validateUserSeckill(request.getUserId(), request.getSeckillId())) {
                throw new SeckillException(400, "您已参加过该秒杀活动");
            }

            // 3. 扣减库存（同步）
            boolean deducted = stockService.deductStock(
                    request.getSeckillId(),
                    request.getQuantity()
            );

            if (!deducted) {
                throw new SeckillException(400, "库存不足");
            }

            // 4. 创建订单
            SeckillOrder order = createOrder(request, activity);

            long duration = System.currentTimeMillis() - startTime;
            log.info("秒杀成功 - orderId: {}, duration: {}ms", order.getOrderNo(), duration);

            return SeckillResponse.success(order.getOrderNo(), activity.getSeckillPrice());

        } catch (SeckillException e) {
            log.warn("秒杀失败：{}", e.getMessage());
            return SeckillResponse.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("系统异常", e);
            return SeckillResponse.fail(500, "系统繁忙，请稍后重试");
        }
    }

    @Override
    public SeckillActivity getSeckillActivity(Long seckillId) {
        return seckillActivityMapper.selectById(seckillId);
    }

    @Override
    public boolean checkSeckillStatus(Long seckillId) {
        SeckillActivity activity = seckillActivityMapper.selectById(seckillId);
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
        SeckillActivity activity = seckillActivityMapper.selectById(seckillId);
        return activity != null ? activity.getRemainStock() : 0;
    }

    @Override
    public boolean validateUserSeckill(Long userId, Long seckillId) {
        LambdaQueryWrapper<SeckillOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeckillOrder::getUserId, userId)
                .eq(SeckillOrder::getSeckillId, seckillId);

        Long count = seckillOrderMapper.selectCount(wrapper);
        return count == 0;
    }

    private SeckillActivity validateSeckill(Long seckillId) {
        SeckillActivity activity = seckillActivityMapper.selectById(seckillId);
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

    private SeckillOrder createOrder(SeckillRequest request, SeckillActivity activity) {
        SeckillOrder order = new SeckillOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(request.getUserId());
        order.setSeckillId(request.getSeckillId());
        order.setProductId(activity.getProductId());
        order.setSeckillPrice(activity.getSeckillPrice());
        order.setQuantity(request.getQuantity());
        order.setOrderStatus(0); // 待处理

        seckillOrderMapper.insert(order);
        return order;
    }

    private String generateOrderNo() {
        return "SK" + System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }


}