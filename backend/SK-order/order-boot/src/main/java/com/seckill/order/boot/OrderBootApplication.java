package com.seckill.order.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.seckill.order.biz.client"})
@ComponentScan(basePackages = {"com.seckill.order"})
@MapperScan("com.seckill.order.biz.mapper")
public class OrderBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderBootApplication.class, args);
        System.out.println("=================================");
        System.out.println("订单服务已启动");
        System.out.println("访问：http://localhost:8083/api/order/health");
        System.out.println("=================================");
    }
}