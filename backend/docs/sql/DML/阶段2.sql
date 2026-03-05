-- 5. 库存扣减记录表（用于Redis缓存一致性）
CREATE TABLE `stock_deduction` (
                                   `id` bigint PRIMARY KEY AUTO_INCREMENT,
                                   `seckill_id` bigint NOT NULL COMMENT '秒杀ID',
                                   `user_id` bigint NOT NULL COMMENT '用户ID',
                                   `order_no` varchar(32) COMMENT '订单号',
                                   `quantity` int NOT NULL COMMENT '扣减数量',
                                   `status` tinyint DEFAULT 0 COMMENT '状态：0-预扣，1-确认，2-释放',
                                   `deduct_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '扣减时间',
                                   KEY `idx_seckill_user` (`seckill_id`, `user_id`),
                                   KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB COMMENT='库存扣减记录';

-- 6. 缓存版本表（简化）
CREATE TABLE `cache_version` (
                                 `id` bigint PRIMARY KEY AUTO_INCREMENT,
                                 `data_type` varchar(30) NOT NULL COMMENT '数据类型：product/seckill',
                                 `data_id` bigint NOT NULL COMMENT '数据ID',
                                 `version` int DEFAULT 1 COMMENT '版本号',
                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 UNIQUE KEY `uk_data` (`data_type`, `data_id`)
) ENGINE=InnoDB COMMENT='缓存版本控制';