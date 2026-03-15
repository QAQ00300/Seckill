
-- ============================================
-- 秒杀系统 - 阶段 1 建库脚本（无 Redis、无 MQ）
-- 说明：根据后端 OrderEO 和 SeckillOrderEO 实体类生成
-- 数据库：seckill
-- 字符集：utf8mb4
-- ============================================

-- 1. 创建数据库
DROP DATABASE IF EXISTS `seckill`;
CREATE DATABASE `seckill`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

USE `seckill`;

-- 1. 用户表（简化）
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
                        `username` varchar(50) NOT NULL COMMENT '用户名',
                        `password` varchar(100) NOT NULL COMMENT '密码（加密）',
                        `phone` varchar(20) NOT NULL COMMENT '手机号',
                        `status` tinyint(4) DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_phone` (`phone`),
                        UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';


-- 2. 商品表（简化）
CREATE TABLE `product` (
                           `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
                           `name` varchar(100) NOT NULL COMMENT '商品名称',
                           `price` decimal(10,2) NOT NULL COMMENT '价格',
                           `category` varchar(20) COMMENT '分类',
                           `stock` int DEFAULT 0 COMMENT '库存',
                           `status` tinyint DEFAULT 1 COMMENT '状态：1-上架，0-下架',
                           `create_time`datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            PRIMARY KEY (`id`),
                           KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品表';



-- ============================================
-- 5. 订单表（普通订单 + 秒杀订单）
-- ============================================
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单 ID',
                         `order_no` varchar(32) NOT NULL COMMENT '订单号（唯一）',
                         `user_id` bigint(20) NOT NULL COMMENT '用户 ID（逻辑关联，无外键）',
                         `product_id` bigint(20) NOT NULL COMMENT '商品 ID（逻辑关联，无外键）',
                         `seckill_id` bigint(20) DEFAULT NULL COMMENT '秒杀活动 ID（普通订单为 NULL）',
                         `unit_price` decimal(10,2) DEFAULT NULL COMMENT '商品单价',
                         `seckill_price` decimal(10,2) DEFAULT NULL COMMENT '秒杀单价',
                         `quantity` int(11) NOT NULL DEFAULT 1 COMMENT '购买数量',
                         `total_price` decimal(10,2) NOT NULL COMMENT '订单总价',
                         `order_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '订单状态：0-待处理，1-成功，2-失败，3-已支付',
                         `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
                         `is_deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_order_no` (`order_no`),
                         KEY `idx_user_id` (`user_id`),
                         KEY `idx_product_id` (`product_id`),
                         KEY `idx_seckill_id` (`seckill_id`),
                         KEY `idx_user_seckill` (`user_id`, `seckill_id`),
                         KEY `idx_create_time` (`create_time`),
                         KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单表';


-- ============================================
-- 4. 核心表：秒杀活动表
-- ============================================
DROP TABLE IF EXISTS `seckill`;
CREATE TABLE `seckill` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀活动 ID',
                           `activity_name` varchar(100) NOT NULL COMMENT '活动名称',
                           `product_id` bigint(20) NOT NULL COMMENT '关联商品 ID（逻辑关联，无外键）',
                           `seckill_price` decimal(10,2) NOT NULL COMMENT '秒杀价格',
                           `seckill_stock` int(11) NOT NULL COMMENT '秒杀总库存',
                           `remain_stock` int(11) NOT NULL DEFAULT 0 COMMENT '剩余库存',
                           `start_time` datetime NOT NULL COMMENT '开始时间',
                           `end_time` datetime NOT NULL COMMENT '结束时间',
                           `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态：1-进行中，0-未开始，-1-已结束',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           KEY `idx_product_id` (`product_id`),
                           KEY `idx_status_time` (`status`, `start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='秒杀活动表';


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

