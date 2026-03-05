package com.seckill.order.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.order.bo.eo.OrderEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<OrderEO> {

    /**
     * 根据订单号查询订单
     */
    @Select("SELECT * FROM `order` WHERE order_no = #{orderNo} AND is_deleted = 0")
    OrderEO selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户ID查询订单列表
     */
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<OrderEO> selectByUserId(@Param("userId") Integer userId);

    /**
     * 根据商品ID查询订单列表
     */
    @Select("SELECT * FROM `order` WHERE product_id = #{productId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<OrderEO> selectByProductId(@Param("productId") Integer productId);

    /**
     * 根据订单状态查询订单列表
     */
    @Select("SELECT * FROM `order` WHERE order_status = #{status} AND is_deleted = 0 ORDER BY create_time DESC")
    List<OrderEO> selectByStatus(@Param("status") Integer status);

    /**
     * 更新订单状态
     */
    @Update("UPDATE `order` SET order_status = #{status} WHERE order_no = #{orderNo} AND is_deleted = 0")
    int updateOrderStatus(@Param("orderNo") String orderNo, @Param("status") Integer status);

    /**
     * 逻辑删除订单
     */
    @Update("UPDATE `order` SET is_deleted = 1 WHERE order_no = #{orderNo}")
    int deleteByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据秒杀活动ID查询订单列表
     */
    @Select("SELECT * FROM `order` WHERE seckill_id = #{seckillId} AND is_deleted = 0")
    List<OrderEO> selectBySeckillId(@Param("seckillId") String seckillId);

    /**
     * 统计用户订单数量
     */
    @Select("SELECT COUNT(1) FROM `order` WHERE user_id = #{userId} AND is_deleted = 0")
    int countOrdersByUserId(@Param("userId") Integer userId);

    /**
     * 统计用户今日订单数量
     */
    @Select("SELECT COUNT(1) FROM `order` " +
            "WHERE user_id = #{userId} " +
            "  AND DATE(create_time) = CURDATE() " +
            "  AND is_deleted = 0")
    int countTodayOrdersByUserId(@Param("userId") Integer userId);

    /**
     * 查询用户某状态下的订单数量
     */
    @Select("SELECT COUNT(1) FROM `order` " +
            "WHERE user_id = #{userId} " +
            "  AND order_status = #{status} " +
            "  AND is_deleted = 0")
    int countOrdersByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") Integer status);
}
