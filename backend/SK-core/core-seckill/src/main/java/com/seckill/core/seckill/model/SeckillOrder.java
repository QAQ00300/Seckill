package com.seckill.core.seckill.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seckill.common.base.bo.BaseEO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀订单实体
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@TableName("`order`")
public class SeckillOrder extends BaseEO implements Serializable {

    @TableField("order_no")
    private String orderNo;

    @TableField("user_id")
    private Long userId;

    @TableField("seckill_id")
    private Long seckillId;

    @TableField("product_id")
    private Long productId;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("seckill_price")
    private BigDecimal seckillPrice;

    @TableField("quantity")
    private Integer quantity;

    @TableField("total_price")
    private BigDecimal totalPrice;

    @TableField("order_status")
    private Integer orderStatus;

    @TableField(value = "pay_time", fill = FieldFill.UPDATE)
    private LocalDateTime payTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}