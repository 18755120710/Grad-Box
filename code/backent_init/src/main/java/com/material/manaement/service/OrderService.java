package com.material.manaement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.material.manaement.model.entity.Order;

public interface OrderService extends IService<Order> {
    /**
     * 生成订单号
     */
    String generateOrderNo();
}
