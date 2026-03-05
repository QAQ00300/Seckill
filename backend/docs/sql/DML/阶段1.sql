-- 1. 用户表（简化）
CREATE TABLE `user` (
                        `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
                        `username` varchar(50) NOT NULL COMMENT '用户名',
                        `password` varchar(100) NOT NULL COMMENT '密码',
                        `phone` varchar(20) NOT NULL COMMENT '手机号',
                        `status` tinyint DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                        UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB COMMENT='用户表';

-- 2. 商品表（简化）
CREATE TABLE `product` (
                           `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
                           `name` varchar(100) NOT NULL COMMENT '商品名称',
                           `price` decimal(10,2) NOT NULL COMMENT '价格',
                           `category` varchar(20) COMMENT '分类',
                           `stock` int DEFAULT 0 COMMENT '库存',
                           `status` tinyint DEFAULT 1 COMMENT '状态：1-上架，0-下架',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='商品表';

-- 3. 秒杀活动表
CREATE TABLE `seckill` (
                           `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '秒杀ID',
                           `product_id` bigint NOT NULL COMMENT '商品ID',
                           `seckill_price` decimal(10,2) NOT NULL COMMENT '秒杀价',
                           `seckill_stock` int NOT NULL COMMENT '秒杀库存',
                           `start_time` datetime NOT NULL COMMENT '开始时间',
                           `end_time` datetime NOT NULL COMMENT '结束时间',
                           `status` tinyint DEFAULT 2 COMMENT '状态：2-未开始，1-进行中，0-已结束',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                           KEY `idx_time_status` (`start_time`, `end_time`, `status`)
) ENGINE=InnoDB COMMENT='秒杀活动表';

-- 4. 订单表（简化，去除支付相关）
CREATE TABLE `order` (
                         `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
                         `order_no` varchar(32) NOT NULL COMMENT '订单号',
                         `user_id` bigint NOT NULL COMMENT '用户ID',
                         `product_id` bigint NOT NULL COMMENT '商品ID',
                         `seckill_id` bigint COMMENT '秒杀ID',
                         `quantity` int DEFAULT 1 COMMENT '数量',
                         `total_price` decimal(10,2) NOT NULL COMMENT '总金额',
                         `order_status` tinyint DEFAULT 0 COMMENT '状态：0-待处理，1-已完成，2-已取消',
                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                         UNIQUE KEY `uk_order_no` (`order_no`),
                         KEY `idx_user_time` (`user_id`, `create_time`)
) ENGINE=InnoDB COMMENT='订单表';