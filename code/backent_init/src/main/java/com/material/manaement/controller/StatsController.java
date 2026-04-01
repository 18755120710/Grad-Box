package com.material.manaement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.material.manaement.common.Result;
import com.material.manaement.model.entity.Order;
import com.material.manaement.service.ConsultationService;
import com.material.manaement.service.OrderService;
import com.material.manaement.service.ProjectService;
import com.material.manaement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
public class StatsController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ConsultationService consultationService;

    @GetMapping("/overview")
    public Result overview() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userService.count());
        stats.put("projectCount", projectService.count());
        stats.put("orderCount", orderService.count());

        // Total Revenue (Status >= 1 means paid/delivered)
        Double totalRevenue = orderService.list(new LambdaQueryWrapper<Order>().ge(Order::getStatus, 1))
                .stream().mapToDouble(Order::getTotalAmount).sum();
        stats.put("totalRevenue", totalRevenue);

        stats.put("consultationCount", consultationService.count());

        return Result.success(stats);
    }
}
