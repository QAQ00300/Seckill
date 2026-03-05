package com.seckill.core.seckill.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.core.seckill.model.SeckillSuccess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 秒杀成功记录Mapper
 */
@Mapper
public interface SeckillSuccessMapper extends BaseMapper<SeckillSuccess> {

    /**
     * 查询用户是否已参与秒杀
     */
    @Select("SELECT COUNT(1) FROM seckill_success " +
            "WHERE user_id = #{userId} AND seckill_id = #{seckillId}")
    int existsByUserAndSeckill(@Param("userId") Long userId,
                               @Param("seckillId") Long seckillId);

    /**
     * 根据订单号查询
     */
    @Select("SELECT * FROM seckill_success WHERE order_no = #{orderNo}")
    SeckillSuccess selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户ID查询秒杀成功记录
     */
    @Select("SELECT * FROM seckill_success " +
            "WHERE user_id = #{userId} " +
            "ORDER BY seckill_time DESC")
    List<SeckillSuccess> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据秒杀活动ID查询成功记录数量
     */
    @Select("SELECT COUNT(1) FROM seckill_success " +
            "WHERE seckill_id = #{seckillId}")
    int countBySeckillId(@Param("seckillId") Long seckillId);

    /**
     * 查询指定时间范围内的秒杀成功记录
     */
    @Select("SELECT * FROM seckill_success " +
            "WHERE seckill_time >= #{startTime} " +
            "  AND seckill_time <= #{endTime} " +
            "ORDER BY seckill_time DESC")
    List<SeckillSuccess> selectByTimeRange(@Param("startTime") Date startTime,
                                           @Param("endTime") Date endTime);
}