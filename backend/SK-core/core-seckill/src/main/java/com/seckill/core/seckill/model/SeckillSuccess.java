package com.seckill.core.seckill.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 秒杀成功记录实体
 */
@Data
@TableName("seckill_success")
public class SeckillSuccess {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long seckillId;

    private String orderNo;

    private LocalDateTime seckillTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}