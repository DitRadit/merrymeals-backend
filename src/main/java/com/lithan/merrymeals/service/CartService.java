package com.lithan.merrymeals.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lithan.merrymeals.entity.Cart;
import com.lithan.merrymeals.entity.Meal;
import com.lithan.merrymeals.entity.MealsUsers;
import com.lithan.merrymeals.exception.BadRequestException;
import com.lithan.merrymeals.repository.CartRepository;
import com.lithan.merrymeals.repository.MealRepository;
import com.lithan.merrymeals.repository.MealsUserRepository;

@Service
public class CartService {
    
    @Autowired
    private MealRepository mealRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private MealsUserRepository userRepo;

    @Transactional
    public Cart addCart(String username, Long mealId, int amounts){
        //Is the meal exist?
        //is the meal already in cart's user?
        //if yes. update the amounts and calculate
        //if no, create a new one

  Meal meal = mealRepository.findById(mealId)
        .orElseThrow(() -> new BadRequestException("Can't find Meals with Id : " + mealId));

Optional<Cart> optional = cartRepository.findByUserEmailAndMealId(username, mealId);

Cart cart;
if (optional.isPresent()) {
    cart = optional.get();
    cart.setAmounts(cart.getAmounts() + amounts);
    cart.setTotal(cart.getTotal() + amounts); // Update the total amount
} else {
    cart = new Cart();
    cart.setAmounts(amounts);
    cart.setMeal(meal);
    cart.setTotal(amounts);
    cart.setTimeMade(new Date());
    MealsUsers user = userRepo.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    cart.setUser(user);
}

cartRepository.save(cart);
return cart;

    }
    public Cart updateAmounts(String username, Long mealId, int amounts) {
        Cart cart = cartRepository.findByUserEmailAndMealId(username, mealId)
            .orElseThrow(() -> new BadRequestException(
                "Can't Find Meal id : " + mealId + " in your cart "));

        cart.setAmounts(amounts);
        cart.setTotal(amounts);
        cartRepository.save(cart);
        return cart;
    }

    public void deleteById(String username, Long mealId) {
        Cart cart = cartRepository.findByUserEmailAndMealId(username, mealId)
            .orElseThrow(() -> new BadRequestException(
                "Can't Find Meal id : " + mealId + " in your cart "));

        cartRepository.delete(cart);
    }

    public List<Cart> findByUserId(String username) {
        return cartRepository.findByUserUserName(username);
       
    }
    
    


}
