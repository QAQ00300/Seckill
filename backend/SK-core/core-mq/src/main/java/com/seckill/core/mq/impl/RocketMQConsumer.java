package com.seckill.core.mq.impl;

import com.seckill.core.mq.MqConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

/**
 * RocketMQ消息消费者实现
 */
@Service
@Slf4j
public class RocketMQConsumer implements MqConsumer {

    @Value("${rocketmq.consumer.group}")
    private String consumerGroup;

    @Value("${rocketmq.name-server}")
    private String nameServer;

    private DefaultMQPushConsumer consumer;

    @PostConstruct
    public void init() {
        try {
            consumer = new DefaultMQPushConsumer(consumerGroup);
            consumer.setNamesrvAddr(nameServer);
            
            // 订阅主题
            consumer.subscribe("seckill-order", "*");
            
            // 注册消息监听器
            consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                for (MessageExt msg : msgs) {
                    try {
                        String message = new String(msg.getBody());
                        log.info("Received message: topic={}, tags={}, body={}", 
                                msg.getTopic(), msg.getTags(), message);
                        
                        // 处理消息，例如订单创建、库存扣减等
                        processMessage(msg.getTopic(), msg.getTags(), message);
                    } catch (Exception e) {
                        log.error("Process message failed", e);
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            
            consumer.start();
            log.info("RocketMQ consumer started successfully");
        } catch (Exception e) {
            log.warn("RocketMQ consumer start failed, will continue without RocketMQ: {}", e.getMessage());
            // 不抛出异常，允许应用继续启动
        }
    }

    @PreDestroy
    public void destroy() {
        if (consumer != null) {
            consumer.shutdown();
            log.info("RocketMQ consumer shutdown");
        }
    }

    private void processMessage(String topic, String tags, String message) {
        // 这里可以根据不同的主题和标签处理不同的消息
        if ("seckill-order".equals(topic)) {
            // 处理秒杀订单消息
            handleSeckillOrderMessage(message);
        }
    }

    private void handleSeckillOrderMessage(String message) {
        // 处理秒杀订单逻辑，例如创建订单、扣减库存等
        log.info("Handling seckill order message: {}", message);
        // 这里可以调用订单服务、库存服务等进行业务处理
    }
}