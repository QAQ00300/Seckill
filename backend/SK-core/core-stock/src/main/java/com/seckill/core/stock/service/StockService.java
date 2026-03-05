package com.seckill.core.stock.service;



import com.seckill.core.stock.model.StockDeductRequest;
import com.seckill.core.stock.model.StockDeductResult;

/**
 * 库存服务接口
 */
public interface StockService {

    /**
     * 扣减库存（数据库版）
     */
    boolean deductStock(Long seckillId, Integer quantity);

    /**
     * 查询库存
     */
    Integer getStock(Long seckillId);

    /**
     * 增加库存
     */
    boolean increaseStock(Long seckillId, Integer quantity);

    /**
     * 锁定库存
     */
    boolean lockStock(Long seckillId, Integer quantity);

    /**
     * 解锁库存
     */
    boolean unlockStock(Long seckillId, Integer quantity);

    /**
     * 批量扣减库存
     */
    StockDeductResult batchDeductStock(StockDeductRequest request);
}
