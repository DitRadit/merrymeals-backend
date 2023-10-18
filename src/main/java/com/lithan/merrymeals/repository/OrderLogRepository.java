package com.lithan.merrymeals.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lithan.merrymeals.entity.OrderLog;

public interface OrderLogRepository extends JpaRepository <OrderLog, Long>{
    
}
