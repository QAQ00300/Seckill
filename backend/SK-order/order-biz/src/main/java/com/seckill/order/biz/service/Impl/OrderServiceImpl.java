package com.seckill.order.biz.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.common.tools.exception.CustomException;
import com.seckill.order.biz.mapper.OrderMapper;
import com.seckill.order.biz.service.OrderService;
import com.seckill.order.bo.eo.OrderEO;
import com.seckill.order.constant.OrderErrorCode;
import com.seckill.order.constant.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEO> implements OrderService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(OrderEO order) {
        validateOrder(order);

        String orderNo = generateOrderNo("ORD");
        order.setOrderNo(Integer.valueOf(orderNo));

        if (order.getOrderStatus() == null) {
            order.setOrderStatus(OrderStatus.PENDING.getCode());
        }

        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        boolean result = save(order);
        if (!result) {
            throw new CustomException(OrderErrorCode.ORDER_CREATE_FAILED.getCode(),
                    OrderErrorCode.ORDER_CREATE_FAILED.getMessage());
        }

        log.info("普通订单创建成功：orderNo={}, userId={}, totalPrice={}",
                orderNo, order.getUserId(), order.getTotalPrice());
        return orderNo;
    }


    @Override
    public OrderEO getOrderByNo(String orderNo) {
        LambdaQueryWrapper<OrderEO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEO::getOrderNo, orderNo);
        return getOne(wrapper);
    }

    @Override
    public List<OrderEO> getOrdersByUserId(Long userId) {
        LambdaQueryWrapper<OrderEO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEO::getUserId, userId)
                .orderByDesc(OrderEO::getCreateTime);
        return list(wrapper);
    }

    @Override
    public Page<OrderEO> pageOrdersByUserId(Long userId, Integer pageNum, Integer pageSize) {
        Page<OrderEO> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OrderEO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEO::getUserId, userId)
                .orderByDesc(OrderEO::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public List<OrderEO> getOrdersByProductId(Long productId) {
        LambdaQueryWrapper<OrderEO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEO::getProductId, productId)
                .orderByDesc(OrderEO::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<OrderEO> getOrdersByStatus(Integer status) {
        LambdaQueryWrapper<OrderEO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEO::getOrderStatus, status)
                .orderByDesc(OrderEO::getCreateTime);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrder(OrderEO order) {
        order.setUpdateTime(LocalDateTime.now());
        return updateById(order);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatus(String orderNo, Integer status) {
        OrderEO order = getOrderByNo(orderNo);
        if (order == null) {
            throw new CustomException(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMessage());
        }

        order.setOrderStatus(status);
        if (OrderStatus.PAID.getCode().equals(status)) {
            order.setPayTime(LocalDateTime.now());
        }
        order.setUpdateTime(LocalDateTime.now());

        return updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(String orderNo) {
        OrderEO order= getOrderByNo(orderNo);
        if (order == null) {
            throw new CustomException(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMessage());
        }

        if (!OrderStatus.PENDING.getCode().equals(order.getOrderStatus())) {
            throw new CustomException(OrderErrorCode.ORDER_CANCEL_NOT_ALLOWED.getCode(),
                    OrderErrorCode.ORDER_CANCEL_NOT_ALLOWED.getMessage());
        }

        order.setOrderStatus(OrderStatus.CANCELLED.getCode());
        order.setUpdateTime(LocalDateTime.now());

        boolean result = updateById(order);
        if (result) {
            log.info("订单已取消：orderNo={}", orderNo);
        }

        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrder(String orderNo) {
        OrderEO order= getOrderByNo(orderNo);
        if (order == null) {
            throw new CustomException(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMessage());
        }

        return removeById(order.getId());
    }

    @Override
    public int countOrdersByUserId(Long userId) {
        LambdaQueryWrapper<OrderEO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEO::getUserId, userId);
        return Math.toIntExact(count(wrapper));
    }

    @Override
    public int countTodayOrdersByUserId(Long userId) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<OrderEO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEO::getUserId, userId)
                .ge(OrderEO::getCreateTime, today.atStartOfDay());
        return Math.toIntExact(count(wrapper));
    }

    @Override
    public int countOrdersByUserIdAndStatus(Long userId, Integer status) {
        LambdaQueryWrapper<OrderEO> wrapper= new LambdaQueryWrapper<>();
        wrapper.eq(OrderEO::getUserId, userId)
                .eq(OrderEO::getOrderStatus, status);
        return Math.toIntExact(count(wrapper));
    }

    @Override
    public BigDecimal calculateTotalAmountByUserId(Long userId) {
        LambdaQueryWrapper<OrderEO> wrapper= new LambdaQueryWrapper<>();
        wrapper.eq(OrderEO::getUserId, userId)
                .in(OrderEO::getOrderStatus, OrderStatus.PAID.getCode(), OrderStatus.COMPLETED.getCode());

        List<OrderEO> orders = list(wrapper);
        BigDecimal total = BigDecimal.ZERO;
        for (OrderEO order: orders) {
            if (order.getTotalPrice() != null) {
                total = total.add(BigDecimal.valueOf(order.getTotalPrice()));
            }
        }
        return total;
    }
    private void validateOrder(OrderEO order) {
        if (order.getUserId() == null || order.getUserId() <= 0) {
            throw new CustomException(OrderErrorCode.USER_ID_INVALID.getCode(),
                    OrderErrorCode.USER_ID_INVALID.getMessage());
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
