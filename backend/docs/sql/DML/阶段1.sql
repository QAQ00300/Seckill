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



-- ============================================
-- 1. 普通订单表
-- ============================================
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
                         `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单 ID',
                         `order_no` VARCHAR(32) NOT NULL COMMENT '订单号（ORD 开头）',
                         `user_id` BIGINT NOT NULL COMMENT '用户 ID',
                         `product_id` BIGINT NOT NULL COMMENT '商品 ID',
                         `unit_price` DECIMAL(10,2) COMMENT '单价',
                         `quantity` INT DEFAULT 1 COMMENT '数量',
                         `total_price` DECIMAL(10,2) NOT NULL COMMENT '总金额',
                         `order_status` TINYINT DEFAULT 0 COMMENT '状态：0-待处理，1-已支付，2-已完成，3-已取消，4-已退款',
                         `pay_time` DATETIME COMMENT '支付时间',
                         `remark` VARCHAR(500) COMMENT '备注',
                         `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         UNIQUE KEY `uk_order_no` (`order_no`),
                         KEY `idx_user_id` (`user_id`),
                         KEY `idx_product_id` (`product_id`),
                         KEY `idx_order_status` (`order_status`),
                         KEY `idx_user_create_time` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='普通订单表';

-- ============================================
-- 2. 秒杀订单表
-- ============================================
DROP TABLE IF EXISTS `seckill_order`;
CREATE TABLE `seckill_order` (
                                 `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单 ID',
                                 `order_no` VARCHAR(32) NOT NULL COMMENT '订单号（SK 开头）',
                                 `user_id` BIGINT NOT NULL COMMENT '用户 ID',
                                 `seckill_id` BIGINT NOT NULL COMMENT '秒杀活动 ID',
                                 `product_id` BIGINT NOT NULL COMMENT '商品 ID',
                                 `unit_price` DECIMAL(10,2) COMMENT '商品原价',
                                 `seckill_price` DECIMAL(10,2) COMMENT '秒杀价格',
                                 `quantity` INT DEFAULT 1 COMMENT '数量',
                                 `total_price` DECIMAL(10,2) NOT NULL COMMENT '总金额',
                                 `order_status` TINYINT DEFAULT 0 COMMENT '状态：0-待处理，1-已支付，2-已完成，3-已取消，4-已退款',
                                 `pay_time` DATETIME COMMENT '支付时间',
                                 `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 UNIQUE KEY `uk_order_no` (`order_no`),
                                 KEY `idx_user_id` (`user_id`),
                                 KEY `idx_seckill_id` (`seckill_id`),
                                 KEY `idx_user_seckill` (`user_id`, `seckill_id`),
                                 KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='秒杀订单表';

-- ============================================
-- 3. 关联表：秒杀活动表（供参考）
-- ============================================
DROP TABLE IF EXISTS `seckill`;

CREATE TABLE `seckill` (
                           `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '秒杀 ID',
                           `product_id` BIGINT NOT NULL COMMENT '商品 ID',
                           `seckill_price` DECIMAL(10,2) NOT NULL COMMENT '秒杀价',
                           `seckill_stock` INT NOT NULL COMMENT '秒杀库存',
                           `start_time` DATETIME NOT NULL COMMENT '开始时间',
                           `end_time` DATETIME NOT NULL COMMENT '结束时间',
                           `status` TINYINT DEFAULT 2 COMMENT '状态：2-未开始，1-进行中，0-已结束',
                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           KEY `idx_time_status` (`start_time`, `end_time`, `status`),
                           KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='秒杀活动表';

