package com.seckill.core.stock.mapper;



import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 库存Mapper
 */
@Mapper
public interface StockMapper {

    /**
     * 乐观锁扣减库存
     * 使用版本号防止超卖
     */
    @Update("UPDATE seckill " +
            "SET remain_stock = remain_stock - #{quantity}, " +
            "    version = version + 1 " +
            "WHERE id = #{seckillId} " +
            "  AND remain_stock >= #{quantity} " +
            "  AND status = 1")  // 只扣减进行中的活动
    int deductStockWithOptimisticLock(@Param("seckillId") Long seckillId,
                                      @Param("quantity") Integer quantity);

    /**
     * 查询库存
     */
    @Select("SELECT remain_stock FROM seckill WHERE id = #{seckillId}")
    Integer selectStockBySeckillId(@Param("seckillId") Long seckillId);

    /**
     * 增加库存
     */
    @Update("UPDATE seckill " +
            "SET remain_stock = remain_stock + #{quantity}, " +
            "    version = version + 1 " +
            "WHERE id = #{seckillId}")
    int increaseStock(@Param("seckillId") Long seckillId,
                      @Param("quantity") Integer quantity);
}