package com.seckill.user.biz.service;


import com.seckill.user.bo.eo.UserEO;
import com.seckill.user.ao.req.AdminResetPasswordREQ;
import com.seckill.user.ao.req.UserRegisterREQ;
import com.seckill.user.ao.req.UserUpdateREQ;
import java.util.List;

public interface UserService {

    /**
     * 用户注册（管理员操作）
     * @param req 注册请求
     * @return 用户 ID
     */
    Long register(UserRegisterREQ req);

    /**
     * 根据 ID 查询用户
     * @param userId 用户 ID
     * @return 用户实体
     */
    UserEO getById(Long userId);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     */
    UserEO getByUsername(String username);

    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户实体
     */
    UserEO getByPhone(String phone);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户实体
     */
    UserEO getByEmail(String email);

    /**
     * 查询所有用户
     * @return 用户列表
     */
    List<UserEO> listAll();

    /**
     * 更新用户信息（管理员操作）
     * @param userId 用户 ID
     * @param req 更新请求
     */
    void update(Long userId, UserUpdateREQ req);

    /**
     * 设置用户状态（管理员操作）
     * @param userId 用户 ID
     * @param status 状态
     */
    void setStatus(Long userId, Integer status);

    /**
     * 管理员重置用户密码
     * @param userId 用户 ID
     * @param req 重置密码请求
     */
    void adminResetPassword(Long userId, AdminResetPasswordREQ req);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return true-存在，false-不存在
     */
    boolean isUsernameExist(String username);

    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @return true-存在，false-不存在
     */
    boolean isPhoneExist(String phone);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return true-存在，false-不存在
     */
    boolean isEmailExist(String email);

    /**
     * 删除用户（管理员操作）
     * @param userId 用户 ID
     */
    void delete(Long userId);
}