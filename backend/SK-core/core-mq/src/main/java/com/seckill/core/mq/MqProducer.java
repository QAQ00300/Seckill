package com.seckill.core.mq;


import com.seckill.core.mq.result.SendResult;
import com.seckill.core.mq.result.TransactionSendResult;

/**
 * 消息队列生产者接口
 * 阶段1：定义接口，为阶段3的RocketMQ实现做准备
 */
public interface MqProducer {

    /**
     * 发送普通消息
     */
    SendResult send(String topic, String tags, Object message);

    /**
     * 发送事务消息
     */
    TransactionSendResult sendTransactionMessage(String topic, String tags, Object message, Object arg);

    /**
     * 发送延迟消息
     */
    SendResult sendDelayMessage(String topic, String tags, Object message, int delayLevel);

    /**
     * 发送顺序消息
     */
    SendResult sendOrderlyMessage(String topic, String tags, Object message, String shardingKey);
}

