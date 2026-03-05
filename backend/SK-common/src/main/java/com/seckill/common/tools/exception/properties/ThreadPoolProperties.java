package com.seckill.common.tools.exception.properties;


import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Component
@ConfigurationProperties(prefix = "thread.pool")
@Data
public class ThreadPoolProperties {
    /**
     * 线程池配置
     */
    @Data
    public static class ThreadPoolConfig {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
        private int keepAliveSeconds;
        private String threadNamePrefix;
        private String rejectedExecutionHandler;
        private boolean waitForTasksToCompleteOnShutdown;
        private int awaitTerminationSeconds;
    }
    /**
     * 业务线程池配置
     */
    private ThreadPoolConfig business = new ThreadPoolConfig();
    /**
     * 通用线程池配置
     */
    private ThreadPoolConfig common = new ThreadPoolConfig();
    /**
     * 消息线程池配置
     */
    private ThreadPoolConfig message = new ThreadPoolConfig();
}
