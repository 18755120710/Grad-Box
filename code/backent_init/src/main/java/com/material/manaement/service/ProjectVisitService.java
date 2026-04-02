package com.material.manaement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.material.manaement.model.entity.ProjectVisit;
import java.util.List;

public interface ProjectVisitService extends IService<ProjectVisit> {
    void recordVisit(Long projectId, Long userId, String ip);

    List<ProjectVisit> getRecentVisitors(Long projectId, int limit);
}
