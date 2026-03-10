package com.seckill.user.ao.req;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class AdminResetPasswordREQ {

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}