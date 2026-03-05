package com.seckill.user.constant;

import lombok.Getter;

@Getter
public enum UserStatus {
    DISABLED(0,"禁用"),
    NORMAL(1,"正常");

    private final int value;
    private final String description;

    UserStatus(int value,String description){
        this.value = value;
        this.description = description;
    }

}
