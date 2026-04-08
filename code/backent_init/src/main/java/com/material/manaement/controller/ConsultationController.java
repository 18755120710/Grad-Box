package com.material.manaement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.manaement.common.Result;
import com.material.manaement.model.entity.Consultation;
import com.material.manaement.model.entity.User;
import com.material.manaement.service.ConsultationService;
import com.material.manaement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import com.material.manaement.model.entity.Project;
import com.material.manaement.service.ProjectService;
import java.util.List;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {
    @Autowired
    private ConsultationService consultationService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;

    @PostMapping
    public Result create(@RequestBody Consultation consultation) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByUsername(username);

        consultation.setUserId(user.getId());
        consultation.setStatus(0); // 待处理
        consultation.setCreateTime(new Date());
        consultationService.save(consultation);

        return Result.success(consultation);
    }

    @GetMapping("/my")
    public Result myConsultations(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByUsername(username);

        Page<Consultation> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Consultation> wrapper = new LambdaQueryWrapper<Consultation>()
                .eq(Consultation::getUserId, user.getId())
                .orderByDesc(Consultation::getCreateTime);

        return Result.success(consultationService.page(page, wrapper));
    }

    @GetMapping
    public Result allConsultations(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long projectId) {
        Page<Consultation> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Consultation> wrapper = new LambdaQueryWrapper<Consultation>();
        if (projectId != null) {
            wrapper.eq(Consultation::getProjectId, projectId);
        }
        wrapper.orderByDesc(Consultation::getPriority)
                .orderByDesc(Consultation::getCreateTime);

        Page<Consultation> result = consultationService.page(page, wrapper);

        // Populate project titles
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            List<Long> projectIds = result.getRecords().stream().map(Consultation::getProjectId).distinct()
                    .collect(Collectors.toList());
            Map<Long, String> projectMap = projectService.listByIds(projectIds).stream()
                    .collect(Collectors.toMap(Project::getId, Project::getTitle));
            result.getRecords().forEach(c -> {
                c.setProjectTitle(projectMap.get(c.getProjectId()));
            });
        }
        return Result.success(result);
    }

    @PutMapping("/{id}/status")
    public Result updateStatus(@PathVariable Long id, @RequestParam Integer status,
            @RequestParam(required = false) String reply) {
        Consultation consultation = new Consultation();
        consultation.setId(id);
        consultation.setStatus(status);
        if (reply != null) {
            consultation.setReply(reply);
        }
        consultationService.updateById(consultation);
        return Result.success(null);
    }
}
