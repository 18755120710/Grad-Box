package com.material.manaement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.material.manaement.mapper.ProjectTechDetailMapper;
import com.material.manaement.model.entity.ProjectTechDetail;
import com.material.manaement.service.ProjectTechDetailService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectTechDetailServiceImpl extends ServiceImpl<ProjectTechDetailMapper, ProjectTechDetail>
        implements ProjectTechDetailService {
    @Override
    public List<ProjectTechDetail> getByProjectId(Long projectId) {
        return list(new LambdaQueryWrapper<ProjectTechDetail>()
                .eq(ProjectTechDetail::getProjectId, projectId)
                .orderByAsc(ProjectTechDetail::getSortOrder));
    }
}
