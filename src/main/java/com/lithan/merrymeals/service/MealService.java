package com.lithan.merrymeals.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lithan.merrymeals.entity.Category;
import com.lithan.merrymeals.entity.Meal;
import com.lithan.merrymeals.entity.MealsUsers;
import com.lithan.merrymeals.exception.BadRequestException;
import com.lithan.merrymeals.exception.ResourceNotFoundException;
import com.lithan.merrymeals.repository.CategoryRepository;
import com.lithan.merrymeals.repository.MealRepository;
import com.lithan.merrymeals.util.ImageUtil;

@Service
public class MealService {
    
    @Autowired
    private MealRepository mealRepo;

    @Autowired
    private CategoryRepository categoryRepo;
    
 
    public List<Meal> getAllMeals() {
        return mealRepo.findAll();
    }

    public Optional<Meal> getMealById(long id) {
        return mealRepo.findById(id);
    }

   public Meal createMeal(Meal meal, MultipartFile file, long categoryId) throws IOException {
        // Validate inputs
        if (meal.getFoodName() == null || meal.getFoodName().isEmpty()) {
            throw new BadRequestException("Food name is required.");
        }

        if (meal.getFoodDescription() == null || meal.getFoodDescription().isEmpty()) {
            throw new BadRequestException("Food description is required.");
        }

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Food image is required.");
        }

        if (categoryId <= 0) {
            throw new BadRequestException("Invalid category ID.");
        }

        Optional<Category> categoryOptional = categoryRepo.findById(categoryId);

        if (categoryOptional.isPresent()) {
            meal.setCategory(categoryOptional.get());
            meal.setPicsName(file.getOriginalFilename());
            meal.setFoodPics(ImageUtil.compressImage(file.getBytes()));

            Meal savedMeal = mealRepo.save(meal);

            if (savedMeal != null) {
                return savedMeal;
            } else {
                throw new ResourceNotFoundException("Failed to save the meal.");
            }
        } else {
            throw new BadRequestException("Category not found with ID: " + categoryId);
        }
    }

    public String uploadImage(MultipartFile file) throws IOException{
      Meal meal = mealRepo.save(Meal.builder()
            .picsName(file.getOriginalFilename())
            .foodPics(ImageUtil.compressImage(file.getBytes())).build());
        
        if (meal != null ){
            return "File upload successfully : " + file.getOriginalFilename();
        }
        return null;
    }

    public byte[] getImage(String fileName){
        Optional<Meal> dbFoodPics =  mealRepo.findByPicsName(fileName);
        byte[] images=ImageUtil.decompressImage(dbFoodPics.get().getFoodPics());
        return images;
    }
    public Meal updateMeal(long id, Meal updatedMeal, MultipartFile file) throws IOException {
        Optional<Meal> existingMealOptional = mealRepo.findById(id);
    
        if (existingMealOptional.isPresent()) {
            Meal existingMeal = existingMealOptional.get();
            existingMeal.setFoodName(updatedMeal.getFoodName());
            existingMeal.setFoodDescription(updatedMeal.getFoodDescription());
    
            // Update category if provided
            if (updatedMeal.getCategory() != null) {
                existingMeal.setCategory(updatedMeal.getCategory());
            }
    
            // Update image if provided
            if (file != null && !file.isEmpty()) {
                existingMeal.setPicsName(file.getOriginalFilename());
                existingMeal.setFoodPics(ImageUtil.compressImage(file.getBytes()));
            }
    
            // Update foodStocks if provided
            if (updatedMeal.getFoodStocks() > 0) {
                existingMeal.setFoodStocks(updatedMeal.getFoodStocks());
            }
    
            // Save the updated meal
            return mealRepo.save(existingMeal);
        } else {
            throw new ResourceNotFoundException("Meal not found with id: " + id);
        }
    }
    

    public Meal updateImage(long id, MultipartFile file) throws IOException {
        Optional<Meal> mealOptional = mealRepo.findById(id);

        if (mealOptional.isPresent()) {
            Meal meal = mealOptional.get();
            meal.setPicsName(file.getOriginalFilename());
            meal.setFoodPics(ImageUtil.compressImage(file.getBytes()));
            return mealRepo.save(meal);
        } else {
            throw new ResourceNotFoundException("Meal not found with id: " + id);
        }
    }

    public void deleteMeal(long id) {
        mealRepo.deleteById(id);
    }

      public byte[] getProfilePicsByMealId(Long id) {
        Optional<Meal> dbMealsMeal = mealRepo.findById(id);
        if (dbMealsMeal.isPresent()) {
            Meal meal = dbMealsMeal.get();
            if (meal.getPicsName() != null) {
                return ImageUtil.decompressImage(meal.getFoodPics());
            } else {
                throw new RuntimeException("Profile picture not found for user with ID: " + id);
            }
        } else {
            throw new RuntimeException("User not found with ID: " + id);
        }
    }

    public List<Meal> searchMealsByKeyword(String keyword) {
        return mealRepo.findByFoodNameContainingIgnoreCaseOrFoodDescriptionContainingIgnoreCase(keyword, keyword);
    }
    


}
