package com.seckill.order.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.order.bo.eo.OrderEO;

import java.util.List;

public interface OrderService extends IService<OrderEO> {

    /**
     * 创建订单
     * @param order 订单信息
     * @return 订单号
     */
    String createOrder(OrderEO order);

    /**
     * 根据订单号查询订单
     * @param orderNo 订单号
     * @return 订单信息
     */
    OrderEO getOrderByNo(String orderNo);

    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<OrderEO> getOrdersByUserId(Integer userId);

    /**
     * 根据商品ID查询订单列表
     * @param productId 商品ID
     * @return 订单列表
     */
    List<OrderEO> getOrdersByProductId(Integer productId);

    /**
     * 根据订单状态查询订单列表
     * @param status 订单状态
     * @return 订单列表
     */
    List<OrderEO> getOrdersByStatus(Integer status);

    /**
     * 更新订单信息
     * @param order 订单信息
     * @return 是否成功
     */
    boolean updateOrder(OrderEO order);

    /**
     * 更新订单状态
     * @param orderNo 订单号
     * @param status 新状态
     * @return 是否成功
     */
    boolean updateOrderStatus(String orderNo, Integer status);

    /**
     * 删除订单（逻辑删除）
     * @param orderNo 订单号
     * @return 是否成功
     */
    boolean deleteOrder(String orderNo);

    /**
     * 根据秒杀活动ID查询订单列表
     * @param seckillId 秒杀活动ID
     * @return 订单列表
     */
    List<OrderEO> getOrdersBySeckillId(String seckillId);

    /**
     * 统计用户订单总数
     * @param userId 用户ID
     * @return 订单数量
     */
    int countOrdersByUserId(Integer userId);

    /**
     * 统计用户今日订单数量
     * @param userId 用户ID
     * @return 今日订单数量
     */
    int countTodayOrdersByUserId(Integer userId);

    /**
     * 统计用户指定状态的订单数量
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单数量
     */
    int countOrdersByUserIdAndStatus(Integer userId, Integer status);
}
