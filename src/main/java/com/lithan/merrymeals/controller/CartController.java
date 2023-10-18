package com.lithan.merrymeals.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lithan.merrymeals.entity.Cart;
import com.lithan.merrymeals.model.CartRequest;
import com.lithan.merrymeals.repository.CartRepository;
import com.lithan.merrymeals.security.service.UserDetailsImpl;
import com.lithan.merrymeals.service.CartService;

@RestController
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
public class CartController {

    @Autowired
    private CartService cartService;
    
    @PostMapping("/createcart")
    @PreAuthorize("hasAuthority('USER')")
    public Cart create(@AuthenticationPrincipal UserDetailsImpl user, @RequestBody CartRequest request) {
        return cartService.addCart(user.getUsername(), request.getMealId(), request.getAmounts());
    }
    
    

    @GetMapping("/cart")
    @PreAuthorize("hasAuthority('USER')")
public List<Cart> findByUserId(@AuthenticationPrincipal UserDetailsImpl user) {
    System.out.println("User : " + user.getEmail()); // Log the email address
    return cartService.findByUserId(user.getEmail());
}


    @PatchMapping("/cart/{mealId}")
    @PreAuthorize("hasAuthority('USER')")
    public Cart update(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable("mealId") Long mealId, @RequestParam("amounts") int amounts) {
        return cartService.updateAmounts(user.getUsername(), mealId, amounts);
    }

    @DeleteMapping("/cart/{mealId}")
     @PreAuthorize("hasAuthority('USER')")
    public void deleteById(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable("mealId") Long mealId) {
        cartService.deleteById(user.getUsername(), mealId);
    }

}
