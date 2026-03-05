package com.seckill.core.mq.constant;


/**
 * MQ主题常量
 */
public class MqTopic {

    // 秒杀相关主题
    public static final String TOPIC_SECKILL = "SK_SECKILL_TOPIC";

    // 秒杀订单主题
    public static final String TOPIC_SECKILL_ORDER = "SK_SECKILL_ORDER_TOPIC";

    // 订单创建主题
    public static final String TOPIC_ORDER_CREATE = "SK_ORDER_CREATE_TOPIC";

    // 库存扣减主题
    public static final String TOPIC_STOCK_DEDUCT = "SK_STOCK_DEDUCT_TOPIC";

    // 支付相关主题
    public static final String TOPIC_PAYMENT = "SK_PAYMENT_TOPIC";

    // 秒杀成功通知
    public static final String TOPIC_SECKILL_SUCCESS = "SK_SECKILL_SUCCESS_TOPIC";

    // 秒杀失败通知
    public static final String TOPIC_SECKILL_FAILED = "SK_SECKILL_FAILED_TOPIC";

    // 标签定义
    public static class Tag {
        // 秒杀订单标签
        public static final String TAG_SECKILL_ORDER = "seckill_order";

        // 订单创建标签
        public static final String TAG_ORDER_CREATE = "order_create";

        // 库存扣减标签
        public static final String TAG_STOCK_DEDUCT = "stock_deduct";

        // 库存回滚标签
        public static final String TAG_STOCK_ROLLBACK = "stock_rollback";

        // 秒杀成功标签
        public static final String TAG_SECKILL_SUCCESS = "seckill_success";

        // 秒杀失败标签
        public static final String TAG_SECKILL_FAILED = "seckill_failed";
    }
}