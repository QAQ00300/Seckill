package com.seckill.order.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.order.bo.eo.SeckillOrderEO;
import java.util.List;

/**
 * 秒杀订单服务接口
 */
public interface SeckillOrderService extends IService<SeckillOrderEO> {

    /**
     * 创建秒杀订单
     */
    String createSeckillOrder(SeckillOrderEO order);

    /**
     * 根据订单号查询订单
     */
    SeckillOrderEO getOrderByNo(String orderNo);

    /**
     * 根据用户 ID 查询订单列表
     */
    List<SeckillOrderEO> getOrdersByUserId(Long userId);

    /**
     * 根据秒杀活动 ID 查询订单列表
     */
    List<SeckillOrderEO> getOrdersBySeckillId(Long seckillId);

    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(String orderNo, Integer status);

    /**
     * 取消订单
     */
    boolean cancelOrder(String orderNo);

    /**
     * 删除订单（逻辑删除）
     */
    boolean deleteOrder(String orderNo);

    /**
     * 验证用户是否已参加过该秒杀
     */
    boolean hasParticipated(Long userId, Long seckillId);
}