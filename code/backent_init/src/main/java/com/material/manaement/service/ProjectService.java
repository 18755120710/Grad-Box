package com.material.manaement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.material.manaement.model.entity.Project;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface ProjectService extends IService<Project> {
    IPage<Project> page(Page<Project> page, String keyword, String sort, Long categoryId, String techStack,
            String major);
}
