package com.seckill.order.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.order.bo.eo.SeckillOrderEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 秒杀订单 Mapper
 */
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrderEO> {

    /**
     * 根据订单号查询订单
     */
    SeckillOrderEO selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户 ID 查询订单列表
     */
    java.util.List<SeckillOrderEO> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据秒杀活动 ID 查询订单列表
     */
    java.util.List<SeckillOrderEO> selectBySeckillId(@Param("seckillId") Long seckillId);

    /**
     * 更新订单状态
     */
    int updateOrderStatus(@Param("orderNo") String orderNo, @Param("status") Integer status);

    /**
     * 逻辑删除订单
     */
    int deleteByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 统计用户订单数量
     */
    long countByUserIdAndSeckillId(@Param("userId") Long userId, @Param("seckillId") Long seckillId);
}