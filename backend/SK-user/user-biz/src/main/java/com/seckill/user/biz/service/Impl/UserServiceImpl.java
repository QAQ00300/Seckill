package com.seckill.user.biz.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.common.tools.exception.CustomException;
import com.seckill.core.cache.CacheService;
import com.seckill.user.biz.mapper.UserMapper;
import com.seckill.user.biz.service.UserService;
import com.seckill.user.bo.eo.UserEO;
import com.seckill.user.constant.UserErrorCode;
import com.seckill.user.constant.UserStatus;
import com.seckill.user.ao.req.AdminResetPasswordREQ;
import com.seckill.user.ao.req.UserRegisterREQ;
import com.seckill.user.ao.req.UserUpdateREQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEO> implements UserService {

    @Autowired
    private CacheService cacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(UserRegisterREQ req) {
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
    public UserEO getById(Long id) {
        if (id == null) {
            return null;
        }
        String cacheKey = "user:id:" + id;
        // 尝试从缓存获取
        UserEO user = cacheService.get(cacheKey, UserEO.class);
        if (user != null) {
            return user;
        }
        // 缓存未命中，从数据库查询
        user = baseMapper.selectById(id);
        if (user != null) {
            // 存入缓存，设置过期时间为1小时
            cacheService.set(cacheKey, user, 3600);
        }
        return user;
    }

    @Override
    public UserEO getByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        String cacheKey = "user:username:" + username;
        // 尝试从缓存获取
        UserEO user = cacheService.get(cacheKey, UserEO.class);
        if (user != null) {
            return user;
        }
        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<UserEO> wrapper= new LambdaQueryWrapper<>();
        wrapper.eq(UserEO::getUsername, username);
        user = getOne(wrapper);
        if (user != null) {
            // 存入缓存，设置过期时间为1小时
            cacheService.set(cacheKey, user, 3600);
        }
        return user;
    }

    @Override
    public UserEO getByPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return null;
        }
        String cacheKey = "user:phone:" + phone;
        // 尝试从缓存获取
        UserEO user = cacheService.get(cacheKey, UserEO.class);
        if (user != null) {
            return user;
        }
        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<UserEO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEO::getPhone, phone);
        user = getOne(wrapper);
        if (user != null) {
            // 存入缓存，设置过期时间为1小时
            cacheService.set(cacheKey, user, 3600);
        }
        return user;
    }

    @Override
    public UserEO getByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        String cacheKey = "user:email:" + email;
        // 尝试从缓存获取
        UserEO user = cacheService.get(cacheKey, UserEO.class);
        if (user != null) {
            return user;
        }
        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<UserEO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEO::getEmail, email);
        user = getOne(wrapper);
        if (user != null) {
            // 存入缓存，设置过期时间为1小时
            cacheService.set(cacheKey, user, 3600);
        }
        return user;
    }

    @Override
    public List<UserEO> listAll() {
        return list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, UserUpdateREQ req) {
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
        // 清除缓存
        String idCacheKey = "user:id:" + userId;
        cacheService.delete(idCacheKey);
        String usernameCacheKey = "user:username:" + user.getUsername();
        cacheService.delete(usernameCacheKey);
        if (user.getPhone() != null) {
            String phoneCacheKey = "user:phone:" + user.getPhone();
            cacheService.delete(phoneCacheKey);
        }
        if (user.getEmail() != null) {
            String emailCacheKey = "user:email:" + user.getEmail();
            cacheService.delete(emailCacheKey);
        }
        log.info("用户信息更新成功，userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setStatus(Long userId, Integer status) {
        UserEO user= getById(userId);
        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        user.setStatus(status);
        updateById(user);
        // 清除缓存
        String idCacheKey = "user:id:" + userId;
        cacheService.delete(idCacheKey);
        String usernameCacheKey = "user:username:" + user.getUsername();
        cacheService.delete(usernameCacheKey);
        if (user.getPhone() != null) {
            String phoneCacheKey = "user:phone:" + user.getPhone();
            cacheService.delete(phoneCacheKey);
        }
        if (user.getEmail() != null) {
            String emailCacheKey = "user:email:" + user.getEmail();
            cacheService.delete(emailCacheKey);
        }
        log.info("用户状态设置成功，userId={}, status={}", userId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminResetPassword(Long userId, AdminResetPasswordREQ req) {
        UserEO user = getById(userId);
        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        user.setPassword(req.getNewPassword());
        updateById(user);
        // 清除缓存
        String idCacheKey = "user:id:" + userId;
        cacheService.delete(idCacheKey);
        String usernameCacheKey = "user:username:" + user.getUsername();
        cacheService.delete(usernameCacheKey);
        if (user.getPhone() != null) {
            String phoneCacheKey = "user:phone:" + user.getPhone();
            cacheService.delete(phoneCacheKey);
        }
        if (user.getEmail() != null) {
            String emailCacheKey = "user:email:" + user.getEmail();
            cacheService.delete(emailCacheKey);
        }
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
    public void delete(Long userId) {
        UserEO user = getById(userId);
        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        // 清除缓存
        String idCacheKey = "user:id:" + userId;
        cacheService.delete(idCacheKey);
        String usernameCacheKey = "user:username:" + user.getUsername();
        cacheService.delete(usernameCacheKey);
        if (user.getPhone() != null) {
            String phoneCacheKey = "user:phone:" + user.getPhone();
            cacheService.delete(phoneCacheKey);
        }
        if (user.getEmail() != null) {
            String emailCacheKey = "user:email:" + user.getEmail();
            cacheService.delete(emailCacheKey);
        }
        removeById(userId);
        log.info("用户删除成功，userId={}", userId);
    }
}