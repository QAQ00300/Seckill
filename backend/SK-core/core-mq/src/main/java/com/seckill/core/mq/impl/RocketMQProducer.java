package com.seckill.core.mq.impl;

import com.seckill.core.mq.MqProducer;
import com.seckill.core.mq.result.SendResult;
import com.seckill.core.mq.result.TransactionSendResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * RocketMQ消息生产者实现
 */
@Service
@Slf4j
public class RocketMQProducer implements MqProducer {

    @Value("${rocketmq.producer.group}")
    private String producerGroup;

    @Value("${rocketmq.name-server}")
    private String nameServer;

    private DefaultMQProducer producer;

    @PostConstruct
    public void init() {
        try {
            producer = new DefaultMQProducer(producerGroup);
            producer.setNamesrvAddr(nameServer);
            producer.start();
            log.info("RocketMQ producer started successfully");
        } catch (Exception e) {
            log.warn("RocketMQ producer start failed, will continue without RocketMQ: {}", e.getMessage());
            // 不抛出异常，允许应用继续启动
        }
    }

    @PreDestroy
    public void destroy() {
        if (producer != null) {
            producer.shutdown();
            log.info("RocketMQ producer shutdown");
        }
    }

    @Override
    public SendResult send(String topic, String tags, Object message) {
        if (producer == null) {
            log.warn("RocketMQ producer is not initialized, returning failed result");
            return new SendResult(null, topic, false, "RocketMQ producer not initialized");
        }
        try {
            Message msg = new Message(topic, tags, message.toString().getBytes());
            org.apache.rocketmq.client.producer.SendResult result = producer.send(msg);
            log.debug("Send message success: topic={}, tags={}, msgId={}", topic, tags, result.getMsgId());
            return new SendResult(result.getMsgId(), topic, true, null);
        } catch (Exception e) {
            log.error("Send message failed: topic={}, tags={}", topic, tags, e);
            return new SendResult(null, topic, false, e.getMessage());
        }
    }

    @Override
    public TransactionSendResult sendTransactionMessage(String topic, String tags, Object message, Object arg) {
        if (producer == null) {
            log.warn("RocketMQ producer is not initialized, returning failed result");
            return new TransactionSendResult(null, topic, false, "RocketMQ producer not initialized", null, null);
        }
        // 暂未实现事务消息
        log.warn("Transaction message not implemented");
        return new TransactionSendResult(null, topic, false, "Not implemented", null, null);
    }

    @Override
    public SendResult sendDelayMessage(String topic, String tags, Object message, int delayLevel) {
        if (producer == null) {
            log.warn("RocketMQ producer is not initialized, returning failed result");
            return new SendResult(null, topic, false, "RocketMQ producer not initialized");
        }
        try {
            Message msg = new Message(topic, tags, message.toString().getBytes());
            msg.setDelayTimeLevel(delayLevel);
            org.apache.rocketmq.client.producer.SendResult result = producer.send(msg);
            log.debug("Send delay message success: topic={}, tags={}, msgId={}, delayLevel={}", 
                      topic, tags, result.getMsgId(), delayLevel);
            return new SendResult(result.getMsgId(), topic, true, null);
        } catch (Exception e) {
            log.error("Send delay message failed: topic={}, tags={}", topic, tags, e);
            return new SendResult(null, topic, false, e.getMessage());
        }
    }

    @Override
    public SendResult sendOrderlyMessage(String topic, String tags, Object message, String shardingKey) {
        if (producer == null) {
            log.warn("RocketMQ producer is not initialized, returning failed result");
            return new SendResult(null, topic, false, "RocketMQ producer not initialized");
        }
        try {
            Message msg = new Message(topic, tags, message.toString().getBytes());
            org.apache.rocketmq.client.producer.SendResult result = producer.send(msg, (mqs, msg1, arg) -> {
                int index = arg.hashCode() % mqs.size();
                return mqs.get(index);
            }, shardingKey);
            log.debug("Send orderly message success: topic={}, tags={}, msgId={}, shardingKey={}", 
                      topic, tags, result.getMsgId(), shardingKey);
            return new SendResult(result.getMsgId(), topic, true, null);
        } catch (Exception e) {
            log.error("Send orderly message failed: topic={}, tags={}", topic, tags, e);
            return new SendResult(null, topic, false, e.getMessage());
        }
    }
}