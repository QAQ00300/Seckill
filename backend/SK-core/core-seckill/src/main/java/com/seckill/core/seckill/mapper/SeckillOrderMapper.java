package com.seckill.core.seckill.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.core.seckill.model.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 秒杀订单Mapper
 */
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {

    /**
     * 根据订单号查询订单
     */
    @Select("SELECT * FROM `order` WHERE order_no = #{orderNo}")
    SeckillOrder selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户ID查询订单列表
     */
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<SeckillOrder> selectByUserId(@Param("userId") Long userId);

    /**
     * 更新订单状态
     */
    @Update("UPDATE `order` SET order_status = #{status} WHERE order_no = #{orderNo}")
    int updateOrderStatus(@Param("orderNo") String orderNo,
                          @Param("status") Integer status);

    /**
     * 根据订单号删除订单
     */
    @Update("UPDATE `order` SET is_deleted = 1 WHERE order_no = #{orderNo}")
    int deleteByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据秒杀活动ID查询订单列表
     */
    @Select("SELECT * FROM `order` WHERE seckill_id = #{seckillId}")
    List<SeckillOrder> selectBySeckillId(@Param("seckillId") Long seckillId);

    /**
     * 统计用户今日订单数量
     */
    @Select("SELECT COUNT(1) FROM `order` " +
            "WHERE user_id = #{userId} " +
            "  AND DATE(create_time) = CURDATE()")
    int countTodayOrdersByUserId(@Param("userId") Long userId);
}
