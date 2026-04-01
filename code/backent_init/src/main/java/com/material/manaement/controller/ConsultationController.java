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

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {
    @Autowired
    private ConsultationService consultationService;
    @Autowired
    private UserService userService;

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
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Consultation> page = new Page<>(pageNum, pageSize);
        return Result.success(consultationService.page(page,
                new LambdaQueryWrapper<Consultation>().orderByDesc(Consultation::getCreateTime)));
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
