package com.material.manaement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.manaement.common.Result;
import com.material.manaement.model.entity.Project;
import com.material.manaement.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @GetMapping
    public Result list(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String techStack,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) Integer status) {
        Page<Project> page = new Page<>(pageNum, pageSize);
        return Result.success(projectService.page(page, keyword, sort, categoryId, techStack, major, status));
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(projectService.getById(id));
    }

    @PostMapping
    public Result create(@RequestBody Project project) {
        projectService.save(project);
        return Result.success(project);
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Project project) {
        project.setId(id);
        projectService.updateById(project);
        return Result.success(project);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        projectService.removeById(id);
        return Result.success(null);
    }
}
