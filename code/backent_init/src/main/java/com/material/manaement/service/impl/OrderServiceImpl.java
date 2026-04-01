package com.material.manaement.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.material.manaement.mapper.OrderMapper;
import com.material.manaement.model.entity.Order;
import com.material.manaement.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Override
    public String generateOrderNo() {
        return "ORD" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(4);
    }
}
