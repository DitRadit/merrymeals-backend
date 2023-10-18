package com.lithan.merrymeals.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lithan.merrymeals.entity.Category;
import com.lithan.merrymeals.service.CategoryService;

// http://localhost:8080/api/category

@RestController
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category")
    public List<Category> findAll(){
        return categoryService.findAll();
    }

    @GetMapping("/category/{id}")
    public Category findById(@PathVariable("id") Long id){
        return categoryService.findById(id);
    }

    @PostMapping("/createcategory")
    @PreAuthorize("hasAuthority('PARTNER')")
    public Category create(@RequestBody Category category){
        return categoryService.create(category);
    }

    @PutMapping("/editcategory/{id}")
    @PreAuthorize("hasAuthority('PARTNER')")
    public Category edit(@PathVariable Long id, @RequestBody Category category){
        return categoryService.edit(id, category);
    }

    @DeleteMapping("/category/{id}")
    @PreAuthorize("hasAuthority('PARTNER')")
    public void deleteById(@PathVariable("id") Long id){
        categoryService.deleteById(id);
    }

}
