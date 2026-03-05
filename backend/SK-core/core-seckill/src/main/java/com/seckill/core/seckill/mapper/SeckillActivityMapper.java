package com.seckill.core.seckill.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.core.seckill.model.SeckillActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;


/**
 * 秒杀活动Mapper
 */
@Mapper
public interface SeckillActivityMapper extends BaseMapper<SeckillActivity> {

    /**
     * 根据ID查询秒杀活动
     */
    @Select("SELECT * FROM seckill WHERE id = #{id} AND status != -1")
    SeckillActivity selectById(@Param("id") Long id);

    /**
     * 查询秒杀库存
     */
    @Select("SELECT seckill_stock FROM seckill WHERE id = #{seckillId}")
    Integer selectStockById(@Param("seckillId") Long seckillId);

    /**
     * 扣减库存
     */
    @Update("UPDATE seckill " +
            "SET seckill_stock = seckill_stock - #{quantity} " +
            "WHERE id = #{seckillId} " +
            "  AND seckill_stock >= #{quantity} " +
            "  AND status = 1")  // 只扣减进行中的活动
    int deductStock(@Param("seckillId") Long seckillId,
                    @Param("quantity") Integer quantity);

    /**
     * 乐观锁扣减库存（带版本号）
     */
    @Update("UPDATE seckill " +
            "SET seckill_stock = seckill_stock - #{quantity}, " +
            "    version = version + 1 " +
            "WHERE id = #{seckillId} " +
            "  AND seckill_stock >= #{quantity} " +
            "  AND version = #{version} " +
            "  AND status = 1")
    int deductStockWithVersion(@Param("seckillId") Long seckillId,
                               @Param("quantity") Integer quantity,
                               @Param("version") Integer version);

    /**
     * 更新秒杀活动状态
     */
    @Update("UPDATE seckill SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 查询进行中的秒杀活动
     */
    @Select("SELECT * FROM seckill " +
            "WHERE status = 1 " +
            "  AND start_time <= NOW() " +
            "  AND end_time >= NOW() " +
            "ORDER BY start_time ASC")
    List<SeckillActivity> selectActiveSeckills();

    /**
     * 根据商品ID查询秒杀活动
     */
    @Select("SELECT * FROM seckill WHERE product_id = #{productId} AND status != -1")
    SeckillActivity selectByProductId(@Param("productId") Long productId);
}
