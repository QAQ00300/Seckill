package com.seckill.core.stock.service.Impl;



import com.seckill.core.cache.CacheService;
import com.seckill.core.cache.DistributedLockService;
import com.seckill.core.stock.mapper.StockMapper;
import com.seckill.core.stock.model.StockDeductRequest;
import com.seckill.core.stock.model.StockDeductResult;
import com.seckill.core.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 库存服务实现（数据库版）
 * 阶段1：直接操作数据库
 */
@Service
@Slf4j
public class StockServiceImpl implements StockService {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private DistributedLockService distributedLockService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(Long seckillId, Integer quantity) {
        if (seckillId == null || quantity == null || quantity <= 0) {
            log.error("库存扣减参数错误: seckillId={}, quantity={}", seckillId, quantity);
            return false;
        }

        // 生成分布式锁key
        String lockKey = "stock:lock:" + seckillId;

        try {
            // 获取分布式锁
            boolean locked = distributedLockService.tryLockWithRetry(lockKey, 10, 3, 50);
            if (!locked) {
                log.warn("获取库存锁失败: seckillId={}", seckillId);
                return false;
            }

            try {
                // 使用数据库乐观锁扣减库存
                int affectedRows = stockMapper.deductStockWithOptimisticLock(seckillId, quantity);

                if (affectedRows > 0) {
                    log.info("库存扣减成功: seckillId={}, quantity={}", seckillId, quantity);
                    // 清除缓存
                    String cacheKey = "stock:" + seckillId;
                    cacheService.delete(cacheKey);
                    // 同时清除秒杀服务中的库存缓存
                    String seckillStockKey = "seckill:stock:" + seckillId;
                    cacheService.delete(seckillStockKey);
                    return true;
                } else {
                    log.warn("库存扣减失败，库存不足或活动不存在: seckillId={}", seckillId);
                    return false;
                }
            } finally {
                // 释放分布式锁
                distributedLockService.unlock(lockKey);
            }

        } catch (Exception e) {
            log.error("库存扣减异常: seckillId={}", seckillId, e);
            // 确保释放锁
            distributedLockService.unlock(lockKey);
            throw new RuntimeException("库存扣减异常", e);
        }
    }

    @Override
    public Integer getStock(Long seckillId) {
        if (seckillId == null) {
            return 0;
        }

        String cacheKey = "stock:" + seckillId;
        // 尝试从缓存获取
        Integer stock = cacheService.get(cacheKey, Integer.class);
        if (stock != null) {
            return stock;
        }

        try {
            stock = stockMapper.selectStockBySeckillId(seckillId);
            stock = stock != null ? stock : 0;
            // 存入缓存，设置过期时间为30秒
            cacheService.set(cacheKey, stock, 30);
            return stock;
        } catch (Exception e) {
            log.error("查询库存异常: seckillId={}", seckillId, e);
            return 0;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean increaseStock(Long seckillId, Integer quantity) {
        if (seckillId == null || quantity == null || quantity <= 0) {
            return false;
        }

        try {
            int affectedRows = stockMapper.increaseStock(seckillId, quantity);
            if (affectedRows > 0) {
                // 清除缓存
                String cacheKey = "stock:" + seckillId;
                cacheService.delete(cacheKey);
                // 同时清除秒杀服务中的库存缓存
                String seckillStockKey = "seckill:stock:" + seckillId;
                cacheService.delete(seckillStockKey);
            }
            return affectedRows > 0;
        } catch (Exception e) {
            log.error("增加库存异常: seckillId={}", seckillId, e);
            throw new RuntimeException("增加库存异常", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean lockStock(Long seckillId, Integer quantity) {
        // 阶段1：简化处理，直接扣减
        return deductStock(seckillId, quantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlockStock(Long seckillId, Integer quantity) {
        // 阶段1：简化处理，直接增加
        return increaseStock(seckillId, quantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockDeductResult batchDeductStock(StockDeductRequest request) {
        StockDeductResult result = new StockDeductResult();
        result.setRequestId(request.getRequestId());
        result.setSuccess(true);

        try {
            for (StockDeductRequest.Item item : request.getItems()) {
                boolean deductSuccess = deductStock(item.getSeckillId(), item.getQuantity());
                if (!deductSuccess) {
                    result.setSuccess(false);
                    result.setErrorCode("STOCK_DEDUCT_FAILED");
                    result.setErrorMessage("库存扣减失败: seckillId=" + item.getSeckillId());
                    break;
                }
            }
        } catch (Exception e) {
            log.error("批量扣减库存异常", e);
            result.setSuccess(false);
            result.setErrorCode("SYSTEM_ERROR");
            result.setErrorMessage("系统异常");
        }

        return result;
    }
}
