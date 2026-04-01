package com.material.manaement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.material.manaement.mapper.ProjectHistoryMapper;
import com.material.manaement.model.entity.Project;
import com.material.manaement.model.entity.ProjectHistory;
import com.material.manaement.service.ProjectHistoryService;
import com.material.manaement.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectHistoryServiceImpl extends ServiceImpl<ProjectHistoryMapper, ProjectHistory>
        implements ProjectHistoryService {

    @Autowired
    private ProjectService projectService;

    @Override
    public void addHistory(Long userId, Long projectId) {
        // Find if history already exists for this user and project
        LambdaQueryWrapper<ProjectHistory> wrapper = new LambdaQueryWrapper<ProjectHistory>()
                .eq(ProjectHistory::getUserId, userId)
                .eq(ProjectHistory::getProjectId, projectId);
        ProjectHistory existing = getOne(wrapper);

        if (existing != null) {
            // Update view time
            existing.setViewTime(new Date());
            updateById(existing);
        } else {
            // Add new history
            ProjectHistory history = new ProjectHistory();
            history.setUserId(userId);
            history.setProjectId(projectId);
            history.setViewTime(new Date());
            history.setCreateTime(new Date());
            save(history);
        }
    }

    @Override
    public Page<ProjectHistory> getHistoryPage(Long userId, String type, Integer pageNum, Integer pageSize) {
        Page<ProjectHistory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ProjectHistory> wrapper = new LambdaQueryWrapper<ProjectHistory>()
                .eq(ProjectHistory::getUserId, userId);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if ("day".equals(type)) {
            wrapper.ge(ProjectHistory::getViewTime, calendar.getTime());
        } else if ("week".equals(type)) {
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            wrapper.ge(ProjectHistory::getViewTime, calendar.getTime());
        } else if ("month".equals(type)) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            wrapper.ge(ProjectHistory::getViewTime, calendar.getTime());
        }

        wrapper.orderByDesc(ProjectHistory::getViewTime);

        Page<ProjectHistory> result = page(page, wrapper);

        // Populate project details
        List<ProjectHistory> records = result.getRecords();
        if (!records.isEmpty()) {
            List<Long> projectIds = records.stream().map(ProjectHistory::getProjectId).collect(Collectors.toList());
            List<Project> projects = projectService.listByIds(projectIds);
            records.forEach(history -> {
                projects.stream()
                        .filter(p -> p.getId().equals(history.getProjectId()))
                        .findFirst()
                        .ifPresent(history::setProject);
            });
        }

        return result;
    }
}
