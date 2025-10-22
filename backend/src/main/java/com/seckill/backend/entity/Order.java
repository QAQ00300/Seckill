package com.seckill.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private Long id;
    private Long productId;
    private Long userId;
    private LocalDateTime createTime;

}


