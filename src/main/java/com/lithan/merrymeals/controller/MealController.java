package com.lithan.merrymeals.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lithan.merrymeals.entity.Meal;
import com.lithan.merrymeals.service.MealService;

@RestController
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
public class MealController {
    
    @Autowired
    private MealService mealService;

    @GetMapping("/meals")
    public List<Meal> getAllMeals() {
        return mealService.getAllMeals();
    }

    @GetMapping("/meals/{id}")
    public ResponseEntity<Meal> getMealById(@PathVariable long id) {
        Optional<Meal> meal = mealService.getMealById(id);
        return meal.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/meals/create")
    @PreAuthorize("hasAuthority('PARTNER')")
    public ResponseEntity<Meal> createMeal(
        @ModelAttribute Meal meal,
        @RequestParam("file") MultipartFile file,
        @RequestParam("categoryId") long categoryId) {
        System.out.println("Received request with meal: " + meal);
        System.out.println("Received request with categoryId: " + categoryId); {
        try {
            Meal createdMeal = mealService.createMeal(meal, file, categoryId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        }
    }

    @PostMapping("/meals/uploadImage")
    @PreAuthorize("hasAuthority('PARTNER')")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String message = mealService.uploadImage(file);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/meals/getImage/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        byte[] imageBytes = mealService.getImage(fileName);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

    @PutMapping("/meals/{id}")
    @PreAuthorize("hasAuthority('PARTNER')")
    public ResponseEntity<Meal> updateMeal(
        @PathVariable long id,
        @ModelAttribute Meal updatedMeal,
        @RequestParam(value = "file", required = false) MultipartFile file) {
    try {
        Meal meal = mealService.updateMeal(id, updatedMeal, file);
        return ResponseEntity.ok(meal);
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

    @PutMapping("/meals/{id}/updateImage")
    @PreAuthorize("hasAuthority('PARTNER')")
    public ResponseEntity<Meal> updateMealImage(@PathVariable long id, @RequestParam("file") MultipartFile file) {
        try {
            Meal updatedMeal = mealService.updateImage(id, file);
            return ResponseEntity.ok(updatedMeal);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/meals/{id}")
    @PreAuthorize("hasAuthority('PARTNER')")
    public ResponseEntity<Void> deleteMeal(@PathVariable long id) {
        mealService.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("meal/{id}/getimage")
    public ResponseEntity<byte[]> getImagebyId(@PathVariable Long id) {
        try {
            byte[] foodPics = mealService.getProfilePicsByMealId(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG); // Set the appropriate content type for the response
            return new ResponseEntity<>(foodPics, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Handle not found scenario
        }
    }

    @GetMapping("meals/search/{keyword}")
    public ResponseEntity<?> searchMeals(@PathVariable String keyword) {
        try {
            List<Meal> meals = mealService.searchMealsByKeyword(keyword);
            return ResponseEntity.ok(meals);
        } catch (Exception e) {
            // Handle the exception and return a custom error response
            String errorMessage = "Failed to process the request: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
    }
    
    
}
