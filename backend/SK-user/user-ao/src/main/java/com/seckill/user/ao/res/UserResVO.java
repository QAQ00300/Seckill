package com.seckill.user.ao.res;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResVO {

    private Integer id;
    private String username;
    private String phone;
    private String email;
    private Integer status;
    private String statusDesc;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}