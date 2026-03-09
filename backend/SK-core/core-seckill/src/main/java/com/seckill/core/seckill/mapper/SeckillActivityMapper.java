package com.seckill.core.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.core.seckill.model.SeckillActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 秒杀活动 Mapper
 */
@Mapper
public interface SeckillActivityMapper extends BaseMapper<SeckillActivity> {

    /**
     * 扣减秒杀库存
     */
    int deductStock(@Param("seckillId") Long seckillId, @Param("quantity") Integer quantity);

    /**
     * 锁定秒杀库存
     */
    int lockStock(@Param("seckillId") Long seckillId, @Param("quantity") Integer quantity);
}