package com.lithan.merrymeals.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lithan.merrymeals.entity.Meal;
import com.lithan.merrymeals.entity.OrderItem;
import com.lithan.merrymeals.entity.Orders;
import com.lithan.merrymeals.model.OrderRequest;
import com.lithan.merrymeals.model.OrderResponse;
import com.lithan.merrymeals.security.service.UserDetailsImpl;
import com.lithan.merrymeals.service.OrderService;

@RestController
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    

    @PostMapping("/createorder")
    @PreAuthorize("hasAuthority('USER')")
    public OrderResponse create(@AuthenticationPrincipal UserDetailsImpl user, @RequestBody OrderRequest request){
      return orderService.create(user.getUsername(), request);
    }

    @PatchMapping("/order/{orderId}/cancel")
    @PreAuthorize("hasAuthority('USER')")
    public Orders cancelOrder(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable("orderId") Long orderId){
        return orderService.cancelOrder(orderId, user.getUsername());
    }

    @PatchMapping("/order/{orderId}/confirm")
    @PreAuthorize("hasAuthority('USER')")
    public Orders confirmOrder(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable("orderId") Long orderId){
        return orderService.confirmationOrder(orderId, user.getUsername());
    }

    @PatchMapping("/order/{orderId}/accept")
    @PreAuthorize("hasAuthority('CAREGIVER')")
    public Orders acceptOrder(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable("orderId") Long orderId){
        return orderService.acceptingOrder(orderId, user.getUsername());
    }

    @PatchMapping("/order/{orderId}/caregiving")
    @PreAuthorize("hasAuthority('CAREGIVER')")
    public Orders caregivingOrder(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable("orderId") Long orderId){
        return orderService.caregivingOrder(orderId, user.getUsername());
    }

      @PatchMapping("/order/{orderId}/finished")
    @PreAuthorize("hasAuthority('CAREGIVER')")
    public Orders finishingOrder(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable("orderId") Long orderId){
        return orderService.finishedOrder(orderId, user.getUsername());
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('USER')")
    public List<Orders> findAllUserOrders(@AuthenticationPrincipal UserDetailsImpl user,
    @RequestParam(name = "page", defaultValue = "0", required = false) int page,
    @RequestParam(name = "limit", defaultValue = "25", required = false) int limit){
        return orderService.findAllUserOrder(user.getUsername(), page, limit);
    }

    @GetMapping("/orders/caregiver")
    public List<Orders> findAllUserOrders(@AuthenticationPrincipal UserDetailsImpl user,
    @RequestParam(name = "filterText", defaultValue = "", required = false) String filterText,
    @RequestParam(name = "page", defaultValue = "0", required = false) int page,
    @RequestParam(name = "limit", defaultValue = "25", required = false) int limit){
        return orderService.search(filterText, page, limit);
    }

      @GetMapping("/orderitem")
    public List<OrderItem> getAllItemsOrder() {
        return orderService.getOrderItemsListTest();
    }




}
