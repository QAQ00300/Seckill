package com.seckill.user.biz.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.common.tools.exception.CustomException;
import com.seckill.user.biz.mapper.UserMapper;
import com.seckill.user.biz.service.UserService;
import com.seckill.user.bo.eo.UserEO;
import com.seckill.user.constant.UserErrorCode;
import com.seckill.user.constant.UserStatus;
import com.seckill.user.ao.req.AdminResetPasswordREQ;
import com.seckill.user.ao.req.UserRegisterREQ;
import com.seckill.user.ao.req.UserUpdateREQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEO> implements UserService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer register(UserRegisterREQ req) {
        // 检查用户名是否存在
        if (isUsernameExist(req.getUsername())) {
            throw new CustomException(UserErrorCode.USERNAME_DUPLICATE);
        }

        // 检查手机号是否存在
        if (isPhoneExist(req.getPhone())) {
            throw new CustomException(UserErrorCode.PHONE_DUPLICATE);
        }

        // 检查邮箱是否存在（如果有提供）
        if (StringUtils.hasText(req.getEmail()) && isEmailExist(req.getEmail())) {
            throw new CustomException(UserErrorCode.EMAIL_DUPLICATE);
        }

        // 创建用户
        UserEO user= UserEO.builder()
                .username(req.getUsername())
                .password(req.getPassword())
                .phone(req.getPhone())
                .email(req.getEmail())
                .status(UserStatus.NORMAL.getValue())
                .build();

        save(user);
        log.info("用户注册成功，userId={}, username={}", user.getId(), user.getUsername());
        return user.getId();
    }

    @Override
    public UserEO getById(Integer id) {
        return baseMapper.selectById(id);
    }

    @Override
    public UserEO getByUsername(String username) {
        LambdaQueryWrapper<UserEO> wrapper= new LambdaQueryWrapper<>();
        wrapper.eq(UserEO::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public UserEO getByPhone(String phone) {
        LambdaQueryWrapper<UserEO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEO::getPhone, phone);
        return getOne(wrapper);
    }

    @Override
    public UserEO getByEmail(String email) {
        LambdaQueryWrapper<UserEO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEO::getEmail, email);
        return getOne(wrapper);
    }

    @Override
    public List<UserEO> listAll() {
        return list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Integer userId, UserUpdateREQ req) {
        UserEO user = getById(userId);
        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        // 如果修改了用户名，检查新用户名是否已存在
        if (StringUtils.hasText(req.getUsername()) &&
                !req.getUsername().equals(user.getUsername()) &&
                isUsernameExist(req.getUsername())) {
            throw new CustomException(UserErrorCode.USERNAME_DUPLICATE);
        }

        // 如果修改了手机号，检查新手机号是否已存在
        if (StringUtils.hasText(req.getPhone()) &&
                !req.getPhone().equals(user.getPhone()) &&
                isPhoneExist(req.getPhone())) {
            throw new CustomException(UserErrorCode.PHONE_DUPLICATE);
        }

        // 如果修改了邮箱，检查新邮箱是否已存在
        if (StringUtils.hasText(req.getEmail()) &&
                !req.getEmail().equals(user.getEmail()) &&
                isEmailExist(req.getEmail())) {
            throw new CustomException(UserErrorCode.EMAIL_DUPLICATE);
        }

        // 更新用户信息
        if (StringUtils.hasText(req.getUsername())) {
            user.setUsername(req.getUsername());
        }
        if (StringUtils.hasText(req.getPhone())) {
            user.setPhone(req.getPhone());
        }
        if (req.getEmail() != null) {
            user.setEmail(req.getEmail());
        }
        if (req.getStatus() != null) {
            user.setStatus(req.getStatus());
        }

        updateById(user);
        log.info("用户信息更新成功，userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setStatus(Integer userId, Integer status) {
        UserEO user= getById(userId);
        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        user.setStatus(status);
        updateById(user);
        log.info("用户状态设置成功，userId={}, status={}", userId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminResetPassword(Integer userId, AdminResetPasswordREQ req) {
        UserEO user = getById(userId);
        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        user.setPassword(req.getNewPassword());
        updateById(user);
        log.info("管理员重置用户密码成功，userId={}", userId);
    }

    @Override
    public boolean isUsernameExist(String username) {
        return count(new LambdaQueryWrapper<UserEO>().eq(UserEO::getUsername, username)) > 0;
    }

    @Override
    public boolean isPhoneExist(String phone) {
        return count(new LambdaQueryWrapper<UserEO>().eq(UserEO::getPhone, phone)) > 0;
    }

    @Override
    public boolean isEmailExist(String email) {
        return count(new LambdaQueryWrapper<UserEO>().eq(UserEO::getEmail, email)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer userId) {
        UserEO user = getById(userId);
        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        removeById(userId);
        log.info("用户删除成功，userId={}", userId);
    }
}