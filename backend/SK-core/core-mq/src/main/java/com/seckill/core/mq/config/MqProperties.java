package com.seckill.core.mq.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息队列配置属性
 */
@Data
@ConfigurationProperties(prefix = "sk.mq")
public class MqProperties {

    /**
     * 是否启用消息队列
     * 阶段1：false（不启用）
     * 阶段3：true（启用RocketMQ）
     */
    private boolean enabled = false;

    /**
     * MQ类型
     * none：无MQ（阶段1、阶段2）
     * rocketmq：RocketMQ（阶段3）
     */
    private String type = "none";

    /**
     * 生产者组名
     */
    private String producerGroup = "sk-producer-group";

    /**
     * 消费者组名
     */
    private String consumerGroup = "sk-consumer-group";

    /**
     * NameServer地址
     */
    private String nameServer = "localhost:9876";

    /**
     * 发送消息超时时间（毫秒）
     */
    private int sendMsgTimeout = 3000;

    /**
     * 发送失败重试次数
     */
    private int retryTimesWhenSendFailed = 2;

    /**
     * 最大消息大小（字节）
     */
    private int maxMessageSize = 4096;  // 4KB

    /**
     * 消息压缩阈值（字节）
     */
    private int compressMsgBodyOverHowMuch = 1024;  // 1KB

    /**
     * 事务消息检查线程池大小
     */
    private int checkThreadPoolMinSize = 1;

    /**
     * 事务消息检查线程池最大大小
     */
    private int checkThreadPoolMaxSize = 1;

    /**
     * 事务消息检查请求队列大小
     */
    private int checkRequestHoldMax = 2000;
}
