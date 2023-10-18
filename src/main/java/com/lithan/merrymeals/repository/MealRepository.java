package com.lithan.merrymeals.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lithan.merrymeals.entity.Meal;

@Repository
public interface MealRepository extends JpaRepository <Meal, Long> {
    
    Optional<Meal> findByPicsName(String fileName);
    
    List<Meal> findByFoodNameContainingIgnoreCaseOrFoodDescriptionContainingIgnoreCase(String foodName, String foodDescription);
}
