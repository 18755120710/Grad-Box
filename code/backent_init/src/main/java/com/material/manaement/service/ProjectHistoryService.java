package com.material.manaement.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.material.manaement.model.entity.ProjectHistory;

public interface ProjectHistoryService extends IService<ProjectHistory> {
    void addHistory(Long userId, Long projectId);

    Page<ProjectHistory> getHistoryPage(Long userId, String type, Integer pageNum, Integer pageSize);
}
