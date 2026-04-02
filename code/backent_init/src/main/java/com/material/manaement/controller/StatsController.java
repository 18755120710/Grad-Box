package com.material.manaement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.material.manaement.common.Result;
import com.material.manaement.model.entity.Order;
import com.material.manaement.service.ConsultationService;
import com.material.manaement.service.OrderService;
import com.material.manaement.service.ProjectService;
import com.material.manaement.service.UserService;
import com.material.manaement.model.entity.Category;
import com.material.manaement.model.entity.Project;
import com.material.manaement.model.entity.UserLog;
import com.material.manaement.service.CategoryService;
import com.material.manaement.service.UserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import cn.hutool.core.date.DateUtil;

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
        @Autowired
        private CategoryService categoryService;
        @Autowired
        private UserLogService userLogService;

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

                // 0. Calculate Trends (Simple: Current 7 days vs previous 7 days or just % of
                // total)
                LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
                java.util.Date sevenDaysAgoDate = java.sql.Date.valueOf(sevenDaysAgo);

                long newUsers = userService.count(new LambdaQueryWrapper<com.material.manaement.model.entity.User>()
                                .ge(com.material.manaement.model.entity.User::getCreateTime, sevenDaysAgoDate));
                long totalUsers = (long) stats.get("userCount");
                stats.put("userTrend", totalUsers > 0 ? (int) ((double) newUsers / totalUsers * 100) : 0);

                long newProjects = projectService.count(new LambdaQueryWrapper<Project>()
                                .ge(Project::getCreateTime, sevenDaysAgoDate));
                long totalProjects = (long) stats.get("projectCount");
                stats.put("projectTrend", totalProjects > 0 ? (int) ((double) newProjects / totalProjects * 100) : 0);

                Double newRevenue = orderService.list(new LambdaQueryWrapper<Order>()
                                .ge(Order::getStatus, 1)
                                .ge(Order::getCreateTime, sevenDaysAgoDate))
                                .stream().mapToDouble(Order::getTotalAmount).sum();
                Double totalRev = (Double) stats.get("totalRevenue");
                stats.put("revenueTrend", totalRev > 0 ? (int) (newRevenue / totalRev * 100) : 0);

                // 1. Category Distribution
                List<Category> categories = categoryService.list();
                List<Map<String, Object>> categoryStats = categories.stream().map(cat -> {
                        Map<String, Object> item = new HashMap<>();
                        long count = projectService
                                        .count(new LambdaQueryWrapper<Project>().eq(Project::getCategoryId,
                                                        cat.getId()));
                        item.put("name", cat.getName());
                        item.put("value", count);
                        return item;
                }).filter(item -> (long) item.get("value") > 0).collect(Collectors.toList());
                stats.put("categoryStats", categoryStats);

                // 2. Traffic Data (Last 7 Days DAU based on logs)
                List<String> dates = new ArrayList<>();
                List<Long> values = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
                for (int i = 6; i >= 0; i--) {
                        LocalDate date = LocalDate.now().minusDays(i);
                        dates.add(date.format(formatter));

                        // DAU: Count unique user IDs for the day
                        long dau = userLogService.list(new LambdaQueryWrapper<UserLog>()
                                        .ge(UserLog::getCreateTime, java.sql.Date.valueOf(date))
                                        .lt(UserLog::getCreateTime, java.sql.Date.valueOf(date.plusDays(1)))
                                        .select(UserLog::getUserId))
                                        .stream()
                                        .filter(log -> log.getUserId() != null)
                                        .map(UserLog::getUserId)
                                        .distinct()
                                        .count();

                        values.add(dau);
                }
                Map<String, Object> trafficData = new HashMap<>();
                trafficData.put("dates", dates);
                trafficData.put("values", values);
                stats.put("trafficData", trafficData);

                // 3. Recent Activities
                List<Map<String, Object>> recentActivities = new ArrayList<>();
                // Latest 5 projects
                projectService.list(
                                new LambdaQueryWrapper<Project>().orderByDesc(Project::getCreateTime).last("limit 5"))
                                .forEach(p -> {
                                        Map<String, Object> activity = new HashMap<>();
                                        activity.put("id", "p" + p.getId());
                                        activity.put("time", cn.hutool.core.date.DateUtil.format(p.getCreateTime(),
                                                        "HH:mm"));
                                        activity.put("user", "Admin");
                                        activity.put("action", "添加了新项目");
                                        activity.put("target", p.getTitle());
                                        recentActivities.add(activity);
                                });

                // Latest 5 orders
                orderService.list(new LambdaQueryWrapper<Order>().orderByDesc(Order::getCreateTime).last("limit 5"))
                                .forEach(o -> {
                                        Map<String, Object> activity = new HashMap<>();
                                        activity.put("id", "o" + o.getId());
                                        activity.put("time", cn.hutool.core.date.DateUtil.format(o.getCreateTime(),
                                                        "HH:mm"));
                                        activity.put("user", "System");
                                        activity.put("action", "生成新订单");
                                        activity.put("target", o.getOrderNo());
                                        recentActivities.add(activity);
                                });

                stats.put("recentActivities", recentActivities.stream()
                                .sorted((a, b) -> ((String) b.get("time")).compareTo((String) a.get("time")))
                                .limit(8).collect(Collectors.toList()));

                // 4. Consultation Status Stats
                Map<String, Object> consultationStats = new HashMap<>();
                long pendingCount = consultationService
                                .count(new LambdaQueryWrapper<com.material.manaement.model.entity.Consultation>()
                                                .eq(com.material.manaement.model.entity.Consultation::getStatus, 0));
                long repliedCount = consultationService
                                .count(new LambdaQueryWrapper<com.material.manaement.model.entity.Consultation>()
                                                .eq(com.material.manaement.model.entity.Consultation::getStatus, 1));
                long hiddenCount = consultationService
                                .count(new LambdaQueryWrapper<com.material.manaement.model.entity.Consultation>()
                                                .eq(com.material.manaement.model.entity.Consultation::getStatus, 2));

                List<Map<String, Object>> consultPie = new ArrayList<>();
                consultPie.add(createPieItem("待处理", pendingCount));
                consultPie.add(createPieItem("已回复", repliedCount));
                consultPie.add(createPieItem("已隐藏", hiddenCount));
                stats.put("consultationPie", consultPie);

                return Result.success(stats);
        }

        private Map<String, Object> createPieItem(String name, long value) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", name);
                item.put("value", value);
                return item;
        }
}
