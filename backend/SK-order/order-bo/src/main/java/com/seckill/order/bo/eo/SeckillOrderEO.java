package com.seckill.order.bo.eo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀订单实体
 */
@Data
@Builder
@TableName("`order`")
public class SeckillOrderEO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long seckillId;

    private Long productId;

    private BigDecimal unitPrice;

    private BigDecimal seckillPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    private Integer orderStatus;

    private LocalDateTime payTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}