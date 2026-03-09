package com.seckill.core.seckill.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀活动实体
 */
@Data
@TableName("seckill")
public class SeckillActivity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String activityName;

    private Long productId;

    private BigDecimal seckillPrice;

    private Integer seckillStock;

    private Integer totalQuantity;

    private Integer remainStock;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}