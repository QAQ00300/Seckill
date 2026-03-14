package com.seckill.user.api.external;

import com.seckill.common.tools.result.Result;
import com.seckill.user.biz.service.UserService;
import com.seckill.user.ao.req.AdminResetPasswordREQ;
import com.seckill.user.ao.req.UserRegisterREQ;
import com.seckill.user.ao.req.UserUpdateREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user/mutation")
@RequiredArgsConstructor
@Api(tags = "用户变更接口 - 管理员操作（POST/PUT/DELETE）")
public class UserMutationController {

    private final UserService userService;

    @PostMapping("/register")
    @ApiOperation("管理员创建用户")
    public Result<Long> register(@Validated @RequestBody UserRegisterREQ req) {
        Long userId = userService.register(req);
        return Result.success(userId);
    }

    @PutMapping("/{id}")
    @ApiOperation("更新用户信息（管理员）")
    public Result<Void> updateUser(@PathVariable Long id,
                                   @Validated @RequestBody UserUpdateREQ req) {
        userService.update(id, req);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @ApiOperation("设置用户状态（管理员）")
    public Result<Void> setUserStatus(@PathVariable Long id,
                                      @RequestParam Integer status) {
        userService.setStatus(id, status);
        return Result.success();
    }

    @PutMapping("/{id}/password")
    @ApiOperation("管理员重置用户密码")
    public Result<Void> adminResetPassword(@PathVariable Long id,
                                           @Validated @RequestBody AdminResetPasswordREQ req) {
        userService.adminResetPassword(id, req);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户（管理员）")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
}