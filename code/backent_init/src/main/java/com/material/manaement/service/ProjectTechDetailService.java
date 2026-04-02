package com.material.manaement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.material.manaement.model.entity.ProjectTechDetail;
import java.util.List;

public interface ProjectTechDetailService extends IService<ProjectTechDetail> {
    List<ProjectTechDetail> getByProjectId(Long projectId);
}
