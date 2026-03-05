package com.seckill.user.biz.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.user.biz.mapper.UserMapper;
import com.seckill.user.biz.service.UserService;
import com.seckill.user.bo.eo.UserEO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEO> implements UserService {


}
