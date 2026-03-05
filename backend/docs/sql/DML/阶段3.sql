-- 7. 消息记录表（简化）
CREATE TABLE `mq_record` (
                             `id` bigint PRIMARY KEY AUTO_INCREMENT,
                             `msg_id` varchar(64) NOT NULL COMMENT '消息ID',
                             `topic` varchar(50) NOT NULL COMMENT '消息主题',
                             `business_key` varchar(100) COMMENT '业务标识',
                             `status` tinyint DEFAULT 0 COMMENT '状态：0-待处理，1-处理中，2-成功，3-失败',
                             `retry_count` int DEFAULT 0 COMMENT '重试次数',
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                             `process_time` datetime COMMENT '处理时间',
                             UNIQUE KEY `uk_msg_id` (`msg_id`),
                             KEY `idx_status_time` (`status`, `create_time`)
) ENGINE=InnoDB COMMENT='消息处理记录';

-- 8. 秒杀成功记录表（用于异步处理）
CREATE TABLE `seckill_success` (
                                   `id` bigint PRIMARY KEY AUTO_INCREMENT,
                                   `user_id` bigint NOT NULL COMMENT '用户ID',
                                   `seckill_id` bigint NOT NULL COMMENT '秒杀ID',
                                   `order_no` varchar(32) NOT NULL COMMENT '订单号',
                                   `seckill_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                   UNIQUE KEY `uk_user_seckill` (`user_id`, `seckill_id`),
                                   KEY `idx_seckill_time` (`seckill_time`)
) ENGINE=InnoDB COMMENT='秒杀成功记录';