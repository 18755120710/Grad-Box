package com.material.manaement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.material.manaement.mapper.UserMapper;
import com.material.manaement.model.entity.User;
import com.material.manaement.security.JwtTokenUtil;
import com.material.manaement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public User register(User user) {
        User userExist = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (userExist != null) {
            return null;
        }
        User newUser = new User();
        BeanUtils.copyProperties(user, newUser);
        newUser.setCreateTime(new Date());
        newUser.setStatus(1);
        newUser.setRole("ROLE_USER");
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        save(newUser);
        return newUser;
    }

    @Override
    public User adminRegister(User user) {
        User userExist = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (userExist != null) {
            return null;
        }
        User newUser = new User();
        BeanUtils.copyProperties(user, newUser);
        newUser.setCreateTime(new Date());
        newUser.setStatus(1);
        newUser.setRole("ROLE_ADMIN"); // Set Admin role
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        save(newUser);
        return newUser;
    }

    @Override
    public String login(String username, String password) {
        return loginWithRoleCheck(username, password, null);
    }

    @Override
    public String adminLogin(String username, String password) {
        return loginWithRoleCheck(username, password, "ROLE_ADMIN");
    }

    private String loginWithRoleCheck(String username, String password, String requiredRole) {
        String token = null;
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            // Role check
            if (requiredRole != null) {
                boolean hasRole = userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(requiredRole));
                if (!hasRole) {
                    throw new BadCredentialsException("权限不足，无法登录管理后台");
                }
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user != null) {
            return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole() != null ? user.getRole().replace("ROLE_", "") : "USER")
                    .build();
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }
}
