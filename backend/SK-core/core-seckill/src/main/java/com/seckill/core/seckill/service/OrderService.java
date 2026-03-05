package com.seckill.core.seckill.service;



import com.seckill.core.seckill.dto.SeckillRequest;
import com.seckill.core.seckill.model.SeckillOrder;

import java.util.List;


/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建秒杀订单
     */
    String createOrder(SeckillRequest request);

    /**
     * 根据订单号查询订单
     */
    SeckillOrder getOrderByNo(String orderNo);

    /**
     * 根据用户ID查询订单列表
     */
    List<SeckillOrder> getOrdersByUserId(Long userId);

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
}