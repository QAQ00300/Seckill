package com.seckill.order.biz.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.common.tools.exception.CustomException;
import com.seckill.order.biz.mapper.SeckillOrderMapper;
import com.seckill.order.biz.service.SeckillOrderService;
import com.seckill.order.bo.eo.SeckillOrderEO;
import com.seckill.order.constant.OrderErrorCode;
import com.seckill.order.constant.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrderEO> implements SeckillOrderService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createSeckillOrder(SeckillOrderEO order) {
        validateSeckillOrder(order);

        String orderNo = generateOrderNo("SK");
        order.setOrderNo(orderNo);

        if (order.getOrderStatus() == null) {
            order.setOrderStatus(OrderStatus.PENDING.getCode());
        }

        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        boolean result = save(order);
        if (!result) {
            throw new CustomException(OrderErrorCode.SECKILL_ORDER_CREATE_FAILED.getCode(),
                    OrderErrorCode.SECKILL_ORDER_CREATE_FAILED.getMessage());
        }

        log.info("秒杀订单创建成功：orderNo={}, userId={}, seckillId={}",
                orderNo, order.getUserId(), order.getSeckillId());

        return orderNo;
    }

    @Override
    public SeckillOrderEO getOrderByNo(String orderNo) {
        LambdaQueryWrapper<SeckillOrderEO> wrapper= new LambdaQueryWrapper<>();
        wrapper.eq(SeckillOrderEO::getOrderNo, orderNo);
        return getOne(wrapper);
    }

    @Override
    public List<SeckillOrderEO> getOrdersByUserId(Long userId) {
        LambdaQueryWrapper<SeckillOrderEO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeckillOrderEO::getUserId, userId)
                .orderByDesc(SeckillOrderEO::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<SeckillOrderEO> getOrdersBySeckillId(Long seckillId) {
        LambdaQueryWrapper<SeckillOrderEO> wrapper= new LambdaQueryWrapper<>();
        wrapper.eq(SeckillOrderEO::getSeckillId, seckillId)
                .orderByDesc(SeckillOrderEO::getCreateTime);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatus(String orderNo, Integer status) {
        SeckillOrderEO order = getOrderByNo(orderNo);
        if (order == null) {
            throw new CustomException(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMessage());
        }

        order.setOrderStatus(status);
        if (status == OrderStatus.PAID.getCode()) {
            order.setPayTime(LocalDateTime.now());
        }
        order.setUpdateTime(LocalDateTime.now());

        return updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(String orderNo) {
        SeckillOrderEO order= getOrderByNo(orderNo);
        if (order == null) {
            throw new CustomException(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMessage());
        }

        if (order.getOrderStatus() != OrderStatus.PENDING.getCode()) {
            throw new CustomException(OrderErrorCode.ORDER_CANCEL_NOT_ALLOWED.getCode(),
                    OrderErrorCode.ORDER_CANCEL_NOT_ALLOWED.getMessage());
        }

        boolean success = updateOrderStatus(orderNo, OrderStatus.CANCELLED.getCode());

        if (success) {
            log.info("秒杀订单已取消，需要回滚库存：orderNo={}, seckillId={}",
                    orderNo, order.getSeckillId());
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrder(String orderNo) {
        SeckillOrderEO order= getOrderByNo(orderNo);
        if (order == null) {
            throw new CustomException(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMessage());
        }

        return removeById(order.getId());
    }

    @Override
    public boolean hasParticipated(Long userId, Long seckillId) {
        long count = getBaseMapper().countByUserIdAndSeckillId(userId, seckillId);
        return count > 0;
    }

    private void validateSeckillOrder(SeckillOrderEO order) {
        if (order.getUserId() == null || order.getUserId() <= 0) {
            throw new CustomException(OrderErrorCode.USER_ID_INVALID.getCode(),
                    OrderErrorCode.USER_ID_INVALID.getMessage());
        }
        if (order.getSeckillId() == null || order.getSeckillId() <= 0) {
            throw new CustomException(OrderErrorCode.SECKILL_ACTIVITY_NOT_FOUND.getCode(),
                    OrderErrorCode.SECKILL_ACTIVITY_NOT_FOUND.getMessage());
        }
        if (order.getProductId() == null || order.getProductId() <= 0) {
            throw new CustomException(OrderErrorCode.PRODUCT_ID_INVALID.getCode(),
                    OrderErrorCode.PRODUCT_ID_INVALID.getMessage());
        }
        if (order.getQuantity() == null || order.getQuantity() <= 0) {
            throw new CustomException(OrderErrorCode.QUANTITY_INVALID.getCode(),
                    OrderErrorCode.QUANTITY_INVALID.getMessage());
        }
        if (order.getTotalPrice() == null || order.getTotalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException(OrderErrorCode.PRICE_INVALID.getCode(),
                    OrderErrorCode.PRICE_INVALID.getMessage());
        }
    }

    private String generateOrderNo(String prefix) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return prefix + timestamp + random;
    }
}