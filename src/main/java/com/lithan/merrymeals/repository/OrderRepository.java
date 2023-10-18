package com.lithan.merrymeals.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lithan.merrymeals.entity.MealsUsers;
import com.lithan.merrymeals.entity.Orders;

public interface OrderRepository extends JpaRepository <Orders, Long> {

    List<Orders> findByUserId(String email, Pageable pageable);

    List<Orders> findByUser(MealsUsers user, Pageable pageable);

    
    List<Orders> findByUserOrCareGiverName(MealsUsers user, MealsUsers careGiver, Pageable pageable);

    



    @Query("SELECT p FROM Orders p WHERE LOWER(p.orderNumber) LIKE %:filterText% OR LOWER(p.user.userName) LIKE %:filterText%")
    List<Orders> search(@Param("filterText") String filterText, Pageable pageable);
    
    
}
