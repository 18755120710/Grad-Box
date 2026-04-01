package com.material.manaement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.manaement.common.Result;
import com.material.manaement.model.entity.ProjectHistory;
import com.material.manaement.model.entity.User;
import com.material.manaement.service.ProjectHistoryService;
import com.material.manaement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
public class ProjectHistoryController {

    @Autowired
    private ProjectHistoryService projectHistoryService;

    @Autowired
    private UserService userService;

    @PostMapping("/add/{projectId}")
    public Result add(@PathVariable Long projectId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByUsername(username);
        if (user == null) {
            return Result.failed("请先登录");
        }
        projectHistoryService.addHistory(user.getId(), projectId);
        return Result.success(null);
    }

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByUsername(username);
        if (user == null) {
            return Result.failed("请先登录");
        }
        Page<ProjectHistory> historyPage = projectHistoryService.getHistoryPage(user.getId(), type, pageNum, pageSize);
        return Result.success(historyPage);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        projectHistoryService.removeById(id);
        return Result.success(null);
    }

    @DeleteMapping("/clear")
    public Result clear() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByUsername(username);
        if (user == null) {
            return Result.failed("请先登录");
        }
        // Simplified clear logic
        projectHistoryService
                .remove(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProjectHistory>()
                        .eq(ProjectHistory::getUserId, user.getId()));
        return Result.success(null);
    }
}
