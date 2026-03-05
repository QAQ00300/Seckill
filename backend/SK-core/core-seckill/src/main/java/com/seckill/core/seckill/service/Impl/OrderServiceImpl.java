package com.seckill.core.seckill.service.Impl;



import com.seckill.core.seckill.dto.SeckillRequest;
import com.seckill.core.seckill.mapper.SeckillActivityMapper;
import com.seckill.core.seckill.mapper.SeckillOrderMapper;
import com.seckill.core.seckill.model.SeckillActivity;
import com.seckill.core.seckill.model.SeckillOrder;
import com.seckill.core.seckill.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务实现
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillActivityMapper seckillActivityMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(SeckillRequest request) {
        try {
            // 1. 生成订单号
            String orderNo = generateOrderNo(request.getUserId());

            // 2. 查询秒杀活动信息
            SeckillActivity seckill = seckillActivityMapper.selectById(request.getSeckillId());
            if (seckill == null) {
                throw new RuntimeException("秒杀活动不存在");
            }

            // 3. 计算总金额
            BigDecimal seckillPrice = seckill.getSeckillPrice();
            BigDecimal totalPrice = seckillPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

            // 4. 创建订单对象
            SeckillOrder order = SeckillOrder.builder()
                    .orderNo(orderNo)
                    .userId(request.getUserId())
                    .productId(seckill.getProductId())
                    .seckillId(request.getSeckillId())
                    .quantity(request.getQuantity())
                    .unitPrice(seckillPrice)
                    .totalPrice(totalPrice)
                    .orderStatus(0)  // 0-待处理
                    .createTime(LocalDateTime.now())
                    .build();

            // 5. 保存订单
            int result = seckillOrderMapper.insert(order);
            if (result <= 0) {
                throw new RuntimeException("订单创建失败");
            }

            log.info("订单创建成功: orderNo={}, userId={}, totalPrice={}",
                    orderNo, request.getUserId(), totalPrice);

            return orderNo;

        } catch (Exception e) {
            log.error("创建订单失败", e);
            throw new RuntimeException("创建订单失败: " + e.getMessage());
        }
    }

    @Override
    public SeckillOrder getOrderByNo(String orderNo) {
        return seckillOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public List<SeckillOrder> getOrdersByUserId(Long userId) {
        return seckillOrderMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatus(String orderNo, Integer status) {
        try {
            int result = seckillOrderMapper.updateOrderStatus(orderNo, status);
            return result > 0;
        } catch (Exception e) {
            log.error("更新订单状态失败: orderNo={}, status={}", orderNo, status, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(String orderNo) {
        try {
            // 1. 更新订单状态为已取消
            boolean updateSuccess = updateOrderStatus(orderNo, 2);  // 2-已取消

            if (updateSuccess) {
                // 2. 回滚库存（这里需要调用库存服务）
                SeckillOrder order = getOrderByNo(orderNo);
                if (order != null) {
                    // 调用库存服务回滚库存
                    // stockService.rollbackStock(order.getSeckillId(), order.getQuantity());
                    log.info("订单已取消，需要回滚库存: orderNo={}, seckillId={}",
                            orderNo, order.getSeckillId());
                }
            }

            return updateSuccess;
        } catch (Exception e) {
            log.error("取消订单失败: orderNo={}", orderNo, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrder(String orderNo) {
        try {
            int result = seckillOrderMapper.deleteByOrderNo(orderNo);
            return result > 0;
        } catch (Exception e) {
            log.error("删除订单失败: orderNo={}", orderNo, e);
            return false;
        }
    }

    /**
     * 生成订单号
     * 格式：SK + 时间戳 + 用户ID后4位 + 随机数
     */
    private String generateOrderNo(Long userId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String userIdPart = String.format("%04d", userId % 10000);
        String random = String.format("%04d", (int)(Math.random() * 10000));

        return "SK" + timestamp + userIdPart + random;
    }
}
