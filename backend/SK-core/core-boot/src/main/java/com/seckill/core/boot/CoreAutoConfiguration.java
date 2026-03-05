package com.seckill.core.boot;



import com.seckill.core.boot.config.SwaggerConfig;
import com.seckill.core.boot.config.ThreadPoolConfig;
import com.seckill.core.boot.exception.GlobalExceptionHandler;
import com.seckill.core.boot.filter.RequestLogFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 秒杀核心自动配置
 */
@Configuration
@Import({
        SwaggerConfig.class,
        ThreadPoolConfig.class
})
public class CoreAutoConfiguration {

    /**
     * 全局异常处理器
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    /**
     * 请求日志过滤器
     */
    @Bean
    public RequestLogFilter requestLogFilter() {
        return new RequestLogFilter();
    }

    /**
     * 秒杀业务线程池
     */
    @Bean("seckillThreadPool")
    public ThreadPoolTaskExecutor seckillThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);       // 核心线程数
        executor.setMaxPoolSize(100);       // 最大线程数
        executor.setQueueCapacity(1000);    // 队列容量
        executor.setThreadNamePrefix("seckill-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}