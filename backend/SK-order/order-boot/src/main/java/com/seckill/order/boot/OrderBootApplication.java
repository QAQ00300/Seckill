package com.seckill.order.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.seckill.order.boot"})
public class OrderBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderBootApplication.class, args);
        System.out.println("=================================");
        System.out.println("订单服务已启动");
        System.out.println("访问：http://localhost:8080/api/order/health");
        System.out.println("=================================");
    }
}