package com.material.manaement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.material.manaement.mapper.ProjectVisitMapper;
import com.material.manaement.model.entity.ProjectVisit;
import com.material.manaement.service.ProjectVisitService;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class ProjectVisitServiceImpl extends ServiceImpl<ProjectVisitMapper, ProjectVisit>
        implements ProjectVisitService {
    @Override
    public void recordVisit(Long projectId, Long userId, String ip) {
        ProjectVisit visit = new ProjectVisit();
        visit.setProjectId(projectId);
        visit.setUserId(userId);
        visit.setIp(ip);
        visit.setCreateTime(new Date());
        save(visit);
    }

    @Override
    public List<ProjectVisit> getRecentVisitors(Long projectId, int limit) {
        return list(new LambdaQueryWrapper<ProjectVisit>()
                .eq(ProjectVisit::getProjectId, projectId)
                .orderByDesc(ProjectVisit::getCreateTime)
                .last("LIMIT " + limit));
    }
}
