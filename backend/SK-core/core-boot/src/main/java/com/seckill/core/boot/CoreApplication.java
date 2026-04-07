package com.seckill.core.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 秒杀核心启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.seckill.core"})
@MapperScan({"com.seckill.core.seckill.mapper", "com.seckill.core.stock.mapper"})
@EnableFeignClients(basePackages = {"com.seckill.core.seckill.client"})
public class CoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }
}