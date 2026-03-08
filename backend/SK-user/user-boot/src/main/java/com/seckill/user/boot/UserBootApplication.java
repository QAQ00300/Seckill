package com.seckill.user.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.seckill.user.biz.mapper")
public class UserBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserBootApplication.class, args);
        System.out.println("=================================");
        System.out.println("用户服务已启动");
        System.out.println("访问：http://localhost:8082/api/user");
        System.out.println("=================================");
    }
}