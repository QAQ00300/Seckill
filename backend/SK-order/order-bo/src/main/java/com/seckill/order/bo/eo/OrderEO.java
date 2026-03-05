package com.seckill.order.bo.eo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seckill.common.base.bo.BaseEO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("order")
public class OrderEO extends BaseEO implements Serializable {

    @TableField("id")
    private Integer Id;

    @TableField("order_no")
    private Integer orderNo;

    @TableField("user_id")
    private Integer userId;

    @TableField("product_id")
    private Integer productId;

    @TableField("seckill_id")
    private String seckillId;

    @TableField("quantity")
    private Integer quantity;

    @TableField("total_price")
    private Double totalPrice;

    @TableField("order_status")
    private Integer orderStatus;




}
