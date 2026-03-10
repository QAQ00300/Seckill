package com.seckill.order.biz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.order.bo.eo.OrderEO;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService extends IService<OrderEO> {

    String createOrder(OrderEO order);

    OrderEO getOrderByNo(String orderNo);

    List<OrderEO> getOrdersByUserId(Long userId);

    Page<OrderEO> pageOrdersByUserId(Long userId, Integer pageNum, Integer pageSize);

    List<OrderEO> getOrdersByProductId(Long productId);

    List<OrderEO> getOrdersByStatus(Integer status);

    boolean updateOrder(OrderEO order);

    boolean updateOrderStatus(String orderNo, Integer status);

    boolean cancelOrder(String orderNo);

    boolean deleteOrder(String orderNo);

    int countOrdersByUserId(Long userId);

    int countTodayOrdersByUserId(Long userId);

    int countOrdersByUserIdAndStatus(Long userId, Integer status);

    BigDecimal calculateTotalAmountByUserId(Long userId);
}