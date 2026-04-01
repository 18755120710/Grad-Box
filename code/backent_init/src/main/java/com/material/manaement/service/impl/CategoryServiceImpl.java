package com.material.manaement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.material.manaement.mapper.CategoryMapper;
import com.material.manaement.model.entity.Category;
import com.material.manaement.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
