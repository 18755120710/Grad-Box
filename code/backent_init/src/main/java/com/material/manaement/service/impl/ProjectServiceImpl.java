package com.material.manaement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.material.manaement.mapper.ProjectMapper;
import com.material.manaement.model.entity.Project;
import com.material.manaement.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    @Autowired
    private com.material.manaement.mapper.ProjectMediaMapper projectMediaMapper;
    @Autowired
    private com.material.manaement.mapper.CategoryMapper categoryMapper;

    @Override
    public IPage<Project> page(Page<Project> page, String keyword, String sort, Long categoryId, String techStack,
            String major) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();

        // Keyword Search (Broad)
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Project::getTitle, keyword)
                    .or().like(Project::getTechStack, keyword)
                    .or().like(Project::getDescription, keyword));
        }

        // Specific Tech Stack Filter
        if (StringUtils.hasText(techStack)) {
            wrapper.like(Project::getTechStack, techStack);
        }

        // Specific Major Filter (Fallback to title/description if column missing or
        // virtual)
        if (StringUtils.hasText(major)) {
            wrapper.and(w -> w.like(Project::getTitle, major)
                    .or().like(Project::getDescription, major));
        }

        // Category Filter
        if (categoryId != null) {
            wrapper.eq(Project::getCategoryId, categoryId);
        }

        // Sorting Logic
        if ("views".equals(sort)) {
            wrapper.orderByDesc(Project::getViewCount);
        } else if ("sales".equals(sort)) {
            wrapper.orderByDesc(Project::getSaleCount);
        } else if ("new".equals(sort)) {
            wrapper.orderByDesc(Project::getCreateTime);
        } else if ("price_asc".equals(sort)) {
            wrapper.orderByAsc(Project::getPrice);
        } else if ("price_desc".equals(sort)) {
            wrapper.orderByDesc(Project::getPrice);
        } else {
            wrapper.orderByDesc(Project::getSortOrder).orderByDesc(Project::getCreateTime);
        }

        IPage<Project> result = page(page, wrapper);

        // Populate category name
        result.getRecords().forEach(project -> {
            if (project.getCategoryId() != null) {
                com.material.manaement.model.entity.Category category = categoryMapper
                        .selectById(project.getCategoryId());
                if (category != null) {
                    project.setCategoryName(category.getName());
                }
            }
        });

        return result;
    }

    @Override
    public Project getById(java.io.Serializable id) {
        Project project = super.getById(id);
        if (project != null) {
            // Populate category name
            if (project.getCategoryId() != null) {
                com.material.manaement.model.entity.Category category = categoryMapper
                        .selectById(project.getCategoryId());
                if (category != null) {
                    project.setCategoryName(category.getName());
                }
            }
            // Populate medias
            project.setMedias(projectMediaMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.material.manaement.model.entity.ProjectMedia>()
                            .eq(com.material.manaement.model.entity.ProjectMedia::getProjectId, project.getId())
                            .orderByAsc(com.material.manaement.model.entity.ProjectMedia::getSortOrder)));
        }
        return project;
    }
}
