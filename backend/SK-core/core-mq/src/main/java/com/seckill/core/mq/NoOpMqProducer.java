package com.seckill.core.mq;



import com.seckill.core.mq.result.SendResult;
import com.seckill.core.mq.result.TransactionSendResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 消息队列空实现
 * 阶段1：消息队列未启用，直接同步处理
 */
@Component
@Primary  // 阶段1使用此实现
@Slf4j
public class NoOpMqProducer implements MqProducer {

    @Override
    public SendResult send(String topic, String tags, Object message) {
        log.debug("消息队列未启用 - 同步处理消息, topic: {}, message: {}", topic, message);

        // 阶段1：直接处理消息，模拟同步处理
        try {
            // 这里可以调用相应的业务处理方法
            handleMessageSynchronously(topic, message);

            return new SendResult("noop-" + System.currentTimeMillis(), topic, true, null);
        } catch (Exception e) {
            log.error("同步处理消息失败", e);
            return new SendResult(null, topic, false, e.getMessage());
        }
    }

    @Override
    public TransactionSendResult sendTransactionMessage(String topic, String tags, Object message, Object arg) {
        log.debug("消息队列未启用 - 同步处理事务消息, topic: {}", topic);

        SendResult sendResult = send(topic, tags, message);

        return new TransactionSendResult(
                sendResult.getMessageId(),
                sendResult.getTopic(),
                sendResult.isSuccess(),
                sendResult.getErrorMsg(),
                "noop-transaction-id",
                "COMMIT"
        );
    }

    @Override
    public SendResult sendDelayMessage(String topic, String tags, Object message, int delayLevel) {
        log.warn("消息队列未启用 - 不支持延迟消息");
        return new SendResult(null, topic, false, "消息队列未启用，不支持延迟消息");
    }

    @Override
    public SendResult sendOrderlyMessage(String topic, String tags, Object message, String shardingKey) {
        log.warn("消息队列未启用 - 不支持顺序消息");
        return new SendResult(null, topic, false, "消息队列未启用，不支持顺序消息");
    }

    /**
     * 同步处理消息（模拟）
     */
    private void handleMessageSynchronously(String topic, Object message) {
        log.info("同步处理消息 - topic: {}, message: {}", topic, message);
        // 阶段1：这里可以调用相应的业务服务
        // 例如：如果topic是秒杀订单，则直接调用订单服务
    }
}