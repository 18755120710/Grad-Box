package com.material.manaement.controller;

import com.material.manaement.common.Result;
import com.material.manaement.model.entity.User;
import com.material.manaement.service.UserService;
import com.material.manaement.service.UserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserLogService userLogService;

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        User registered = userService.register(user);
        if (registered == null) {
            return Result.failed("用户名已存在");
        }
        return Result.success(registered);
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpServletRequest request) {
        String token = userService.login(user.getUsername(), user.getPassword());
        if (token == null) {
            return Result.failed("用户名或密码错误");
        }

        // Log activity for DAU
        User loginUser = userService
                .getOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getUsername, user.getUsername()));
        if (loginUser != null) {
            userLogService.log(loginUser.getId(), "LOGIN", "用户登录", request.getRemoteAddr());
        }

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", "Bearer ");
        return Result.success(tokenMap);
    }

    @PostMapping("/admin/login")
    public Result adminLogin(@RequestBody User user, HttpServletRequest request) {
        String token = userService.adminLogin(user.getUsername(), user.getPassword());
        if (token == null) {
            return Result.failed("用户名或密码错误或权限不足");
        }

        // Log activity for DAU
        User loginUser = userService
                .getOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getUsername, user.getUsername()));
        if (loginUser != null) {
            userLogService.log(loginUser.getId(), "ADMIN_LOGIN", "管理员登录", request.getRemoteAddr());
        }

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", "Bearer ");
        return Result.success(tokenMap);
    }

    @PostMapping("/admin/register")
    public Result adminRegister(@RequestBody User user) {
        User registered = userService.adminRegister(user);
        if (registered == null) {
            return Result.failed("用户名已存在");
        }
        return Result.success(registered);
    }
}
