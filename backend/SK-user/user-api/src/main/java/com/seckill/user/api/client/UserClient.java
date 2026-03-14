package com.seckill.user.api.client;

import com.seckill.common.tools.result.Result;
import com.seckill.user.ao.res.UserResVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务 Feign 客户端接口
 * 用于 order 服务调用 user 服务的能力
 *
 * 使用场景示例：
 * - 创建订单前验证用户状态
 * - 查询订单时补充用户信息
 */
@FeignClient(name = "sk-user", contextId = "orderUserClient")
public interface UserClient {

    /**
     * 根据用户 ID 查询用户信息
     * @param userId 用户 ID
     * @return 用户信息
     */
    @GetMapping("/api/user/{id}")
    Result<UserResVO> getUserById(@PathVariable("id") Long userId);

    /**
     * 验证用户状态是否正常
     * @param userId 用户 ID
     * @return true-正常，false-异常
     */
    @GetMapping("/api/user/validate/{id}")
    Result<Boolean> validateUserStatus(@PathVariable("id") Long userId);


}
