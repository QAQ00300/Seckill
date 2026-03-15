package com.seckill.order.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.order.bo.eo.OrderEO;
import org.apache.ibatis.annotations.Mapper;


/**
 * 订单 Mapper 接口
 * 完全依赖 MyBatis-Plus，无需自定义 SQL
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEO> {
    // 所有 CRUD 操作都可以用 BaseMapper 的方法 + LambdaQueryWrapper 实现
}
