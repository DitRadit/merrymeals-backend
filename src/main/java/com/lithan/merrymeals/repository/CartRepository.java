package com.lithan.merrymeals.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lithan.merrymeals.entity.Cart;

public interface CartRepository extends JpaRepository <Cart, Long> {

    Optional<Cart> findByUserEmailAndMealId(String email, Long mealId);

    List<Cart> findByUserUserName(String email);
    
}
