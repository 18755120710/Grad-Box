package com.material.manaement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.manaement.common.Result;
import com.material.manaement.model.entity.Order;
import com.material.manaement.model.entity.User;
import com.material.manaement.service.OrderService;
import com.material.manaement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @PostMapping
    public Result create(@RequestBody Order order) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByUsername(username);

        order.setUserId(user.getId());
        order.setOrderNo(orderService.generateOrderNo());
        order.setStatus(0); // 待支付/待处理
        order.setCreateTime(new Date());
        orderService.save(order);

        return Result.success(order);
    }

    @GetMapping("/my")
    public Result myOrders(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByUsername(username);

        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, user.getId())
                .orderByDesc(Order::getCreateTime);

        return Result.success(orderService.page(page, wrapper));
    }

    @GetMapping
    public Result allOrders(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        return Result
                .success(orderService.page(page, new LambdaQueryWrapper<Order>().orderByDesc(Order::getCreateTime)));
    }

    @PutMapping("/{id}/status")
    public Result updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Order order = new Order();
        order.setId(id);
        order.setStatus(status);
        orderService.updateById(order);
        return Result.success(null);
    }
}
