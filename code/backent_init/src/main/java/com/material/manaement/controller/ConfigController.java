package com.material.manaement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.material.manaement.common.Result;
import com.material.manaement.model.entity.Project;
import com.material.manaement.mapper.ProjectMapper;
import com.material.manaement.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/configs")
public class ConfigController {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping("/home")
    public Result getHomeConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("title", sysConfigService.getValue("home_title") != null ? sysConfigService.getValue("home_title")
                : "成就卓越 毕业作品");
        config.put("subtitle",
                sysConfigService.getValue("home_subtitle") != null ? sysConfigService.getValue("home_subtitle")
                        : "汇集顶级毕业课题，涵盖高质源码与全栈思维指导。每一份交付，都是对技术灵魂的深度刻画。");
        config.put("bannerUrl", sysConfigService.getValue("home_banner_url") != null
                ? sysConfigService.getValue("home_banner_url")
                : "https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?q=80&w=2070&auto=format&fit=crop");

        List<Project> recommended = projectMapper.selectList(
                new LambdaQueryWrapper<Project>()
                        .eq(Project::getIsRecommend, 1)
                        .orderByDesc(Project::getSortOrder)
                        .last("LIMIT 6"));
        config.put("recommendedProjects", recommended);

        return Result.success(config);
    }

    @GetMapping("/system")
    public Result getSystemConfig() {
        Map<String, Object> config = new HashMap<>();
        config.getOrDefault("siteTitle", sysConfigService.getValue("site_title"));
        config.put("siteTitle",
                sysConfigService.getValue("site_title") != null ? sysConfigService.getValue("site_title")
                        : "GradBox Admin");
        config.put("tagline", sysConfigService.getValue("tagline") != null ? sysConfigService.getValue("tagline")
                : "High Performance Project Hub");
        config.put("logoUrl", sysConfigService.getValue("logo_url"));
        config.put("maintenanceMode", "1".equals(sysConfigService.getValue("maintenance_mode")));
        config.put("userApproval", "1".equals(sysConfigService.getValue("user_approval")));
        config.put("sessionTimeout",
                Integer.parseInt(sysConfigService.getValue("session_timeout") != null
                        ? sysConfigService.getValue("session_timeout")
                        : "120"));
        return Result.success(config);
    }

    @PostMapping("/system")
    public Result saveSystemConfig(@RequestBody Map<String, Object> config) {
        sysConfigService.setValue("site_title", (String) config.get("siteTitle"));
        sysConfigService.setValue("tagline", (String) config.get("tagline"));
        sysConfigService.setValue("logo_url", (String) config.get("logoUrl"));
        sysConfigService.setValue("maintenance_mode", (Boolean) config.get("maintenanceMode") ? "1" : "0");
        sysConfigService.setValue("user_approval", (Boolean) config.get("userApproval") ? "1" : "0");
        sysConfigService.setValue("session_timeout", String.valueOf(config.get("sessionTimeout")));
        return Result.success(null);
    }
}
