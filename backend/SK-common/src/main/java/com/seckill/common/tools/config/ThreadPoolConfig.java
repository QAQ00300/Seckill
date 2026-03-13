package com.seckill.common.tools.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.seckill.common.tools.exception.properties.ThreadPoolProperties;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class ThreadPoolConfig {

    @Resource
    private ThreadPoolProperties threadPoolProperties;

    @Bean("CoreExecutor")
    public Executor CoreExecutor() {
        ThreadPoolTaskExecutor origin = createThreadPoolExecutor(threadPoolProperties.getBusiness(),"核心业务线程池");
        return TtlExecutors.getTtlExecutor(origin);
    }

    private ThreadPoolTaskExecutor createThreadPoolExecutor(ThreadPoolProperties.ThreadPoolConfig threadPoolConfig, String poolName) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolConfig.getCorePoolSize());
        executor.setMaxPoolSize(threadPoolConfig.getMaxPoolSize());
        executor.setQueueCapacity(threadPoolConfig.getQueueCapacity());
        executor.setKeepAliveSeconds(threadPoolConfig.getKeepAliveSeconds());
        executor.setThreadNamePrefix(threadPoolConfig.getThreadNamePrefix());
        executor.setRejectedExecutionHandler(getRejectExecutionHandler(threadPoolConfig.getRejectedExecutionHandler()));
        executor.setWaitForTasksToCompleteOnShutdown(threadPoolConfig.isWaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(threadPoolConfig.getAwaitTerminationSeconds());
        executor.initialize();
        log.info("{}初始化成功", poolName);
        return executor;

    }

    private RejectedExecutionHandler getRejectExecutionHandler(String handlerName) {
        return switch (handlerName){
            case "CallerRunsPolicy" -> new ThreadPoolExecutor.CallerRunsPolicy();
            case "DiscardOldestPolicy" -> new ThreadPoolExecutor.DiscardOldestPolicy();
            case "DiscardPolicy" -> new ThreadPoolExecutor.DiscardPolicy();
            default -> new ThreadPoolExecutor.AbortPolicy();
        };

    }
}
