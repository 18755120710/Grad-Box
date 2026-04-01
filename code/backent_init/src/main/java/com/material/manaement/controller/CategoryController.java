package com.material.manaement.controller;

import com.material.manaement.common.Result;
import com.material.manaement.model.entity.Category;
import com.material.manaement.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result list() {
        List<Category> list = categoryService.list();
        return Result.success(list);
    }

    @PostMapping
    public Result create(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success(category);
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.updateById(category);
        return Result.success(category);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success(null);
    }
}
