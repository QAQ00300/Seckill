package com.seckill.order.biz.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.order.biz.mapper.OrderMapper;
import com.seckill.order.biz.service.OrderService;
import com.seckill.order.bo.eo.OrderEO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEO> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(OrderEO order) {
        try {
            // 生成订单号
            String orderNo = generateOrderNo(order.getUserId());
            order.setOrderNo(Integer.valueOf(orderNo));

            // 设置默认状态
            if (order.getOrderStatus() == null) {
                order.setOrderStatus(0); // 0-待支付
            }

            // 保存订单
            boolean result = save(order);
            if (result) {
                log.info("订单创建成功: orderNo={}, userId={}, totalPrice={}",
                        orderNo, order.getUserId(), order.getTotalPrice());
                return orderNo;
            } else {
                throw new RuntimeException("订单创建失败");
            }
        } catch (Exception e) {
            log.error("创建订单失败", e);
            throw new RuntimeException("创建订单失败: " + e.getMessage());
        }
    }

    @Override
    public OrderEO getOrderByNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public List<OrderEO> getOrdersByUserId(Integer userId) {
        return orderMapper.selectByUserId(userId);
    }

    @Override
    public List<OrderEO> getOrdersByProductId(Integer productId) {
        return orderMapper.selectByProductId(productId);
    }

    @Override
    public List<OrderEO> getOrdersByStatus(Integer status) {
        return orderMapper.selectByStatus(status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrder(OrderEO order) {
        try {
            boolean result = updateById(order);
            if (result) {
                log.info("订单更新成功: orderNo={}", order.getOrderNo());
            }
            return result;
        } catch (Exception e) {
            log.error("更新订单失败: orderNo={}", order.getOrderNo(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatus(String orderNo, Integer status) {
        try {
            int result = orderMapper.updateOrderStatus(orderNo, status);
            if (result > 0) {
                log.info("订单状态更新成功: orderNo={}, status={}", orderNo, status);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新订单状态失败: orderNo={}, status={}", orderNo, status, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrder(String orderNo) {
        try {
            int result = orderMapper.deleteByOrderNo(orderNo);
            if (result > 0) {
                log.info("订单删除成功: orderNo={}", orderNo);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除订单失败: orderNo={}", orderNo, e);
            return false;
        }
    }

    @Override
    public List<OrderEO> getOrdersBySeckillId(String seckillId) {
        return orderMapper.selectBySeckillId(seckillId);
    }

    @Override
    public int countOrdersByUserId(Integer userId) {
        return orderMapper.countOrdersByUserId(userId);
    }

    @Override
    public int countTodayOrdersByUserId(Integer userId) {
        return orderMapper.countTodayOrdersByUserId(userId);
    }

    @Override
    public int countOrdersByUserIdAndStatus(Integer userId, Integer status) {
        return orderMapper.countOrdersByUserIdAndStatus(userId, status);
    }

    /**
     * 生成订单号
     * 格式：时间戳 + 用户ID后4位 + 随机数
     */
    private String generateOrderNo(Integer userId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String userIdPart = String.format("%04d", userId % 10000);
        String random = String.format("%04d", (int)(Math.random() * 10000));

        return timestamp + userIdPart + random;
    }
}
