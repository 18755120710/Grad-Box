package com.material.manaement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.manaement.common.Result;
import com.material.manaement.model.entity.User;
import com.material.manaement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.material.manaement.service.ProjectHistoryService;
import com.material.manaement.service.ConsultationService;
import com.material.manaement.model.entity.ProjectHistory;
import com.material.manaement.model.entity.Consultation;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectHistoryService projectHistoryService;

    @Autowired
    private ConsultationService consultationService;

    @GetMapping("/stats")
    public Result getStats() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByUsername(username);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        Map<String, Object> stats = new HashMap<>();
        stats.put("historyCount", projectHistoryService.count(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProjectHistory>()
                        .eq(ProjectHistory::getUserId, user.getId())));
        stats.put("consultationCount", consultationService.count(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Consultation>()
                        .eq(Consultation::getUserId, user.getId())));
        stats.put("favoriteCount", 0); // Placeholder
        return Result.success(stats);
    }

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(User::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return Result.success(userService.page(page, wrapper));
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userService.updateById(user);
        return Result.success("状态更新成功");
    }

    @GetMapping("/info")
    public Result getInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByUsername(username);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        // 隐藏密码
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/update")
    public Result update(@RequestBody User user) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.getByUsername(username);
        if (currentUser == null) {
            return Result.failed("用户不存在");
        }

        // 仅允许修改昵称、头像和简介
        User updateParam = new User();
        updateParam.setId(currentUser.getId());
        updateParam.setNickname(user.getNickname());
        updateParam.setAvatar(user.getAvatar());
        updateParam.setBio(user.getBio());

        userService.updateById(updateParam);
        return Result.success(updateParam);
    }
}
