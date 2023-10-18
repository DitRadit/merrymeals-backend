package com.lithan.merrymeals.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lithan.merrymeals.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository <OrderItem, Long> {
    
}
