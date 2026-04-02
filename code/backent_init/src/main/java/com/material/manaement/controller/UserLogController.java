package com.material.manaement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.manaement.common.Result;
import com.material.manaement.model.entity.UserLog;
import com.material.manaement.service.UserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/user-logs")
public class UserLogController {
    @Autowired
    private UserLogService userLogService;

    @GetMapping("/list")
    public Result list(@RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<UserLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLog::getUserId, userId).orderByDesc(UserLog::getCreateTime);
        return Result.success(userLogService.page(page, queryWrapper));
    }
}
