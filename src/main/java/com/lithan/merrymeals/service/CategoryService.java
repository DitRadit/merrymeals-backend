package com.lithan.merrymeals.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lithan.merrymeals.entity.Category;
import com.lithan.merrymeals.exception.ResourceNotFoundException;
import com.lithan.merrymeals.repository.CategoryRepository;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepo;

    public Category findById(Long id){
        System.out.println("Finding category with ID: " + id);
        return categoryRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Can't find category with id " + id));
    }

    public List<Category> findAll(){
        return categoryRepo.findAll();
    }

    public Category create(Category category){
        return categoryRepo.save(category);
    }

    public Category edit(Long id, Category updatedCategory) {
        Category existingCategory = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    
        existingCategory.setName(updatedCategory.getName());
    
        return categoryRepo.save(existingCategory);
    }

    public void deleteById(Long id) {
        Optional<Category> categoryOptional = categoryRepo.findById(id);
        if (categoryOptional.isPresent()) {
            categoryRepo.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }
    }
}
