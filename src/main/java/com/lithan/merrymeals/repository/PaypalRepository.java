package com.lithan.merrymeals.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lithan.merrymeals.entity.Donation;

public interface PaypalRepository extends JpaRepository<Donation, Long> {
    
}
