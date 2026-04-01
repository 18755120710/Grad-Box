package com.material.manaement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.material.manaement.model.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService extends IService<User> {
    /**
     * 根据用户名获取用户信息
     */
    User getByUsername(String username);

    /**
     * 注册
     */
    User register(User user);

    /**
     * 管理员注册
     */
    User adminRegister(User user);

    /**
     * 登录
     * 
     * @return 生成的JWT token
     */
    String login(String username, String password);

    /**
     * 管理员登录
     *
     * @return 生成的JWT token
     */
    String adminLogin(String username, String password);

    /**
     * 获取用户信息
     */
    UserDetails loadUserByUsername(String username);
}
