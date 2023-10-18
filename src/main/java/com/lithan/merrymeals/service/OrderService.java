package com.lithan.merrymeals.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.lithan.merrymeals.entity.Meal;
import com.lithan.merrymeals.entity.MealsUsers;
import com.lithan.merrymeals.entity.OrderItem;
import com.lithan.merrymeals.entity.Orders;
import com.lithan.merrymeals.exception.BadRequestException;
import com.lithan.merrymeals.model.CartRequest;
import com.lithan.merrymeals.model.OrderRequest;
import com.lithan.merrymeals.model.OrderResponse;
import com.lithan.merrymeals.model.OrderStatus;
import com.lithan.merrymeals.repository.MealRepository;
import com.lithan.merrymeals.repository.MealsUserRepository;
import com.lithan.merrymeals.repository.OrderItemRepository;
import com.lithan.merrymeals.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private MealRepository mealRepo;
    @Autowired
    private OrderRepository orderRepo;
    @Autowired
    private OrderItemRepository itemRepo;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderLogService orderLogService;
    @Autowired
    private MealsUserRepository userRepo;
    
    @Transactional
    public OrderResponse create(String email, OrderRequest request) {
        try {
            MealsUsers user = userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));
    
            Orders order = new Orders();
            order.setDate(new Date());
            order.setOrderNumber(generateNumberOrder());
            order.setUser(user);
            int totalAmounts = request.getItems().stream()
                    .mapToInt(CartRequest::getAmounts)
                    .sum();
            order.setAmounts(totalAmounts);
            order.setAddress(request.getAddress());
            order.setAddCaregiver(request.isAddCaregiver());
            order.setLatitude(request.getLatitude());
            order.setLongitude(request.getLongitude());
            order.setStatus(OrderStatus.DRAFT);
            order.setTimeMade(new Date());
    
            List<OrderItem> items = new ArrayList<>();
            for (CartRequest k : request.getItems()) {
                Meal meal = mealRepo.findById(k.getMealId())
                .orElseThrow(() -> new BadRequestException("Meal with ID: " + k.getMealId() + " not found"));
    
                if (meal.getFoodStocks() < k.getAmounts()) {
                    throw new BadRequestException("Food Stock is not enough for meal: " + meal.getFoodName());
                }
    
                OrderItem pi = new OrderItem();
                pi.setMeal(meal); // Set the Meal object for OrderItem
                pi.setFoodName(meal.getFoodName());
                pi.setAmounts(k.getAmounts());
                pi.setOrder(order);
                items.add(pi);
            }
    
            // Save the order and associated items in a transaction
            Orders savedOrder = orderRepo.save(order);
            for (OrderItem orderItem : items) {
                itemRepo.save(orderItem);
                Meal meal = orderItem.getMeal();
                meal.setFoodStocks(meal.getFoodStocks() - orderItem.getAmounts());
                mealRepo.save(meal);
                cartService.deleteById(email, meal.getId());
            }
    
            // Log the order creation
            orderLogService.createLog(email, savedOrder, OrderLogService.DRAFT, "Order Successfully Created");
    
            return new OrderResponse(savedOrder, items);
        } catch (Exception e) {
            // Handle exceptions, log them, and potentially rethrow custom exception types
            // Rollback transaction if needed
            throw new RuntimeException("Error creating order: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public Orders cancelOrder(Long orderId, String email){
        Orders order = orderRepo.findById(orderId)
            .orElseThrow(() -> new BadRequestException("Order ID : " + orderId + " Can't be founded"));
        if(!email.equals(order.getUser().getEmail())){
            throw  new BadRequestException ("You are unauthorized to perform this action!");
        }

        if (!OrderStatus.DRAFT.equals(order.getStatus())){
            throw new BadRequestException("Can only Cancel DRAFT orders.");
        }
        order.setStatus(OrderStatus.CANCELLED);
        Orders saved = orderRepo.save(order);
        orderLogService.createLog(email, saved, OrderLogService.CANCELLED, "Order Cancelled!");

        return saved;
    }

     @Transactional
     public Orders acceptOrder(Long orderId, String email){
        Orders order = orderRepo.findById(orderId)
            .orElseThrow(() -> new BadRequestException("Order ID : " + orderId + " Can't be founded"));
        if(!email.equals(order.getUser().getUserName())){
            throw  new BadRequestException ("You are unauthorized to perform this action!");
        }

        if (!OrderStatus.PREPARING.equals(order.getStatus())){
            throw new BadRequestException("Ordering failed, status order currently : " + order.getStatus());
        }

        order.setStatus(OrderStatus.DELIVERING);
        Orders saved = orderRepo.save(order);
        orderLogService.createLog(email, saved, OrderLogService.DELIVERING, "Order Cancelled!");
        

        return saved;
    }

       @Transactional
     public Orders confirmationOrder(Long orderId, String email){
        Orders order = orderRepo.findById(orderId)
            .orElseThrow(() -> new BadRequestException("Order ID : " + orderId + " Can't be founded"));
    

        if (!OrderStatus.DRAFT.equals(order.getStatus())){
            throw new BadRequestException("Confirmation failed, status order currently : " + order.getStatus());
        }
        order.setStatus(OrderStatus.PREPARING);
        Orders saved = orderRepo.save(order);
        orderLogService.createLog(email, saved, OrderLogService.PREPARING, "Preparing order");

        return saved;
    }

        @Transactional
     public Orders acceptingOrder(Long orderId, String email){
        Orders order = orderRepo.findById(orderId)
            .orElseThrow(() -> new BadRequestException("Order ID : " + orderId + " Can't be founded"));


        if (!OrderStatus.PREPARING.equals(order.getStatus())){
            throw new BadRequestException("Preparing failed, status order currently : " + order.getStatus());
        }

        order.setCareGiverName(userRepo.findByEmail(email)
            .orElseThrow(() -> new BadRequestException("Caregiver with email " + email + " not found")));

        order.setStatus(OrderStatus.DELIVERING);
        Orders saved = orderRepo.save(order);
        orderLogService.createLog(email, saved, OrderLogService.DELIVERING, "Delivering order");

        return saved;
    }

     @Transactional
     public Orders caregivingOrder(Long orderId, String email){
        Orders order = orderRepo.findById(orderId)
            .orElseThrow(() -> new BadRequestException("Order ID : " + orderId + " Can't be founded"));
       

        if (!OrderStatus.DELIVERING.equals(order.getStatus())){
            throw new BadRequestException("Delivering failed, status order currently : " + order.getStatus());
        }
        order.setStatus(OrderStatus.CAREGIVING);
        Orders saved = orderRepo.save(order);
        orderLogService.createLog(email, saved, OrderLogService.CAREGIVING, "Delivering order");

        return saved;
    }

        @Transactional
        public Orders finishedOrder(Long orderId, String email){
        Orders order = orderRepo.findById(orderId)
            .orElseThrow(() -> new BadRequestException("Order ID : " + orderId + " Can't be founded"));
      
        if (!OrderStatus.CAREGIVING.equals(order.getStatus())){
            throw new BadRequestException("Caregiving failed, status order currently : " + order.getStatus());
        }
        order.setStatus(OrderStatus.FINISHED);
        Orders saved = orderRepo.save(order);
        orderLogService.createLog(email, saved, OrderLogService.FINISHED, "Order Finished");

        return saved;
    }
     
    public List<Orders> findAllUserOrder(String email, int page, int limit) {
        Optional<MealsUsers> userOptional = userRepo.findByEmail(email);
        if (userOptional.isPresent()) {
            MealsUsers user = userOptional.get();
            // Fetch orders where the user is either the primary user or the caregiver
            return orderRepo.findByUserOrCareGiverName(user, user, 
                PageRequest.of(page, limit, Sort.by("timeMade").descending()));
        }
        return Collections.emptyList(); // Return empty list if user not found
    }
    
    
    public List<Orders> search(String filterText, int page, int limit){
        return orderRepo.search(filterText.toLowerCase(), 
        PageRequest.of(page, limit, Sort.by("timeMade").descending()));
    }

    private String generateNumberOrder() {
    UUID uuid = UUID.randomUUID();
    String uniqueNumber = uuid.toString().replace("-", "").substring(0, 10); // Get the first 10 characters of the UUID
    return "ORD" + uniqueNumber; // Concatenate a prefix (e.g., "ORD") with the unique part of the UUID
}

public List<OrderItem> getOrderItemsListTest() {
    return itemRepo.findAll();
}
  

}
