package com.seckill.user.api.external;

import com.seckill.common.tools.result.Result;
import com.seckill.user.biz.service.UserService;
import com.seckill.user.bo.eo.UserEO;
import com.seckill.user.constant.UserStatus;
import com.seckill.user.ao.res.UserResVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/user/query")
@RequiredArgsConstructor
@Api(tags = "用户查询接口 - 管理员操作（GET）")
public class UserQueryController {

    private final UserService userService;

    @GetMapping("/{id}")
    @ApiOperation("根据 ID 查询用户")
    public Result<UserResVO> getUserById(@PathVariable Long id) {
        UserEO user = userService.getById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        UserResVO vo = convertToVO(user);
        return Result.success(vo);
    }

    @GetMapping("/username/{username}")
    @ApiOperation("根据用户名查询用户")
    public Result<UserResVO> getUserByUsername(@PathVariable String username) {
        UserEO user = userService.getByUsername(username);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        UserResVO vo = convertToVO(user);
        return Result.success(vo);
    }

    @GetMapping("/phone/{phone}")
    @ApiOperation("根据手机号查询用户")
    public Result<UserResVO> getUserByPhone(@PathVariable String phone) {
        UserEO user = userService.getByPhone(phone);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        UserResVO vo = convertToVO(user);
        return Result.success(vo);
    }

    @GetMapping("/email/{email}")
    @ApiOperation("根据邮箱查询用户")
    public Result<UserResVO> getUserByEmail(@PathVariable String email) {
        UserEO user = userService.getByEmail(email);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        UserResVO vo = convertToVO(user);
        return Result.success(vo);
    }

    @GetMapping("/list")
    @ApiOperation("查询所有用户")
    public Result<List<UserResVO>> listUsers() {
        List<UserEO> users = userService.listAll();
        List<UserResVO> vos = users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(vos);
    }

    @GetMapping("/check/username/{username}")
    @ApiOperation("检查用户名是否存在")
    public Result<Boolean> checkUsername(@PathVariable String username) {
        boolean exists = userService.isUsernameExist(username);
        return Result.success(exists);
    }

    @GetMapping("/check/phone/{phone}")
    @ApiOperation("检查手机号是否存在")
    public Result<Boolean> checkPhone(@PathVariable String phone) {
        boolean exists = userService.isPhoneExist(phone);
        return Result.success(exists);
    }

    @GetMapping("/check/email/{email}")
    @ApiOperation("检查邮箱是否存在")
    public Result<Boolean> checkEmail(@PathVariable String email) {
        boolean exists = userService.isEmailExist(email);
        return Result.success(exists);
    }

    private UserResVO convertToVO(UserEO user) {
        UserResVO vo = new UserResVO();
        BeanUtils.copyProperties(user, vo);
        vo.setStatusDesc(getStatusDesc(user.getStatus()));
        return vo;
    }

    private String getStatusDesc(Integer status) {
        if (status == null) {
            return null;
        }
        for (UserStatus s : UserStatus.values()) {
            if (s.getValue() == status) {
                return s.getDescription();
            }
        }
        return null;
    }
}