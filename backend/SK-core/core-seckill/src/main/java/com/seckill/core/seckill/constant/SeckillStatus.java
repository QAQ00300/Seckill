package com.seckill.core.seckill.constant;

import lombok.Getter;

@Getter
public enum SeckillStatus {

    ;
    private final String description;

    SeckillStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
