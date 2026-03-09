package com.seckill.core.seckill.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.core.seckill.model.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀订单 Mapper
 */
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {
}