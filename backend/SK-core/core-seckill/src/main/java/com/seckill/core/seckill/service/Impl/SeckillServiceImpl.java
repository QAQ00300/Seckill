package com.seckill.core.seckill.service.Impl;



import com.seckill.core.cache.CacheService;
import com.seckill.core.mq.MqProducer;
import com.seckill.core.seckill.dto.SeckillRequest;
import com.seckill.core.seckill.dto.SeckillResponse;
import com.seckill.core.seckill.mapper.SeckillActivityMapper;
import com.seckill.core.seckill.mapper.SeckillSuccessMapper;
import com.seckill.core.seckill.model.SeckillActivity;
import com.seckill.core.seckill.service.OrderService;
import com.seckill.core.seckill.service.SeckillService;
import com.seckill.core.seckill.validator.SeckillValidator;
import com.seckill.core.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 秒杀服务实现（同步版）
 * 阶段1：直接同步处理所有逻辑
 */
@Service
@Slf4j
public class
SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillValidator seckillValidator;

    @Autowired
    private CacheService cacheService;  // 阶段1：空实现

    @Autowired
    
    private MqProducer mqProducer;      // 阶段1：空实现

    @Autowired
    private OrderService orderService;  // 订单服务

    @Autowired
    private StockService stockService;  // 库存服务

    @Autowired
    private SeckillActivityMapper seckillMapper;

    @Autowired
    private SeckillSuccessMapper seckillSuccessMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeckillResponse processSeckill(SeckillRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("开始处理秒杀请求: {}", request);

        SeckillResponse response = new SeckillResponse();
        response.setRequestId(request.getRequestId());
        response.setUserId(request.getUserId());
        response.setSeckillId(request.getSeckillId());

        try {
            // 1. 基础参数验证
            seckillValidator.validateRequest(request);

            // 2. 验证用户
            if (!validateUser(request.getUserId())) {
                response.setSuccess(false);
                response.setErrorCode("USER_INVALID");
                response.setErrorMessage("用户不存在或已禁用");
                return response;
            }

            // 3. 验证秒杀活动
            SeckillActivity seckill = getSeckillActivity(request.getSeckillId());
            if (seckill == null) {
                response.setSuccess(false);
                response.setErrorCode("SECKILL_NOT_FOUND");
                response.setErrorMessage("秒杀活动不存在");
                return response;
            }

            // 4. 检查活动状态
            if (!checkSeckillStatus(seckill)) {
                response.setSuccess(false);
                response.setErrorCode("SECKILL_STATUS_INVALID");
                response.setErrorMessage("秒杀活动不可用");
                return response;
            }

            // 5. 检查库存（查数据库）
            if (seckill.getSeckillStock() <= 0) {
                response.setSuccess(false);
                response.setErrorCode("STOCK_NOT_ENOUGH");
                response.setErrorMessage("库存不足");
                return response;
            }

            // 6. 检查是否重复购买（查数据库）
            if (seckillSuccessMapper.existsByUserAndSeckill(request.getUserId(), request.getSeckillId())) {
                response.setSuccess(false);
                response.setErrorCode("REPEAT_SECKILL");
                response.setErrorMessage("请勿重复购买");
                return response;
            }

            // 7. 扣减库存（数据库事务）
            boolean deductSuccess = stockService.deductStock(request.getSeckillId(), request.getQuantity());
            if (!deductSuccess) {
                response.setSuccess(false);
                response.setErrorCode("STOCK_DEDUCT_FAILED");
                response.setErrorMessage("库存扣减失败");
                return response;
            }

            // 8. 创建订单（数据库事务）
            String orderNo = orderService.createOrder(request);
            if (orderNo == null) {
                throw new RuntimeException("订单创建失败");
            }

            // 9. 记录秒杀成功（数据库事务）
            SeckillSuccess success = SeckillSuccess.builder()
                    .userId(request.getUserId())
                    .seckillId(request.getSeckillId())
                    .orderNo(orderNo)
                    .seckillTime(LocalDateTime.now())
                    .build();
            seckillSuccessMapper.insert(success);

            // 10. 返回成功结果
            response.setSuccess(true);
            response.setOrderNo(orderNo);
            response.setSeckillPrice(seckill.getSeckillPrice());
            response.setTotalPrice(seckill.getSeckillPrice() * request.getQuantity());
            response.setProcessTime(System.currentTimeMillis() - startTime);

            log.info("秒杀成功: userId={}, seckillId={}, orderNo={}, 耗时={}ms",
                    request.getUserId(), request.getSeckillId(), orderNo, response.getProcessTime());

        } catch (Exception e) {
            log.error("秒杀处理异常", e);
            response.setSuccess(false);
            response.setErrorCode("SYSTEM_ERROR");
            response.setErrorMessage("系统异常，请稍后重试");

            // 这里需要回滚库存（如果有部分操作成功）
            // 实际项目中应该有更完善的事务回滚机制
        }

        return response;
    }

    @Override
    public SeckillActivity getSeckillActivity(Long seckillId) {
        // 阶段1：直接查数据库
        return seckillMapper.selectById(seckillId);
    }

    @Override
    public boolean checkSeckillStatus(Long seckillId) {
        SeckillActivity seckill = getSeckillActivity(seckillId);
        return checkSeckillStatus(seckill);
    }

    private boolean checkSeckillStatus(SeckillActivity seckill) {
        if (seckill == null) return false;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = seckill.getStartTime();
        LocalDateTime endTime = seckill.getEndTime();

        // 状态：2-未开始，1-进行中，0-已结束
        if (now.isBefore(startTime)) {
            seckill.setStatus(2);  // 未开始
            return false;
        } else if (now.isAfter(endTime)) {
            seckill.setStatus(0);  // 已结束
            return false;
        } else {
            seckill.setStatus(1);  // 进行中
            return true;
        }
    }

    @Override
    public Integer getSeckillStock(Long seckillId) {
        SeckillActivity seckill = getSeckillActivity(seckillId);
        return seckill != null ? seckill.getSeckillStock() : 0;
    }

    @Override
    public boolean validateUserSeckill(Long userId, Long seckillId) {
        // 阶段1：简单验证，后续可扩展
        // 1. 检查用户状态
        // 2. 检查用户购买次数限制
        // 3. 检查黑名单等
        return true;
    }

    private boolean validateUser(Long userId) {
        // 阶段1：简单验证，实际应从用户服务获取
        return userId != null && userId > 0;
    }
}