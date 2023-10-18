package com.lithan.merrymeals.service;

import java.util.Date;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Service;

import com.lithan.merrymeals.entity.MealsUsers;
import com.lithan.merrymeals.entity.OrderLog;
import com.lithan.merrymeals.entity.Orders;
import com.lithan.merrymeals.repository.MealsUserRepository;
import com.lithan.merrymeals.repository.OrderLogRepository;

@Service
public class OrderLogService {

    @Autowired
    private OrderLogRepository orderRepo;
    @Autowired
    private MealsUserRepository userRepo;

    public final static int DRAFT = 0;
    public final static int PREPARING = 10;
    public final static int DELIVERING = 20;
    public final static int CAREGIVING = 30;
    public final static int FINISHED = 40;
    public final static int CANCELLED = 90;
    

     public void createLog(String email, Orders order, int type, String message) {
        MealsUsers user = userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));

        OrderLog p = new OrderLog();
        p.setLogMessages(message);
        p.setLogType(type);
        p.setOrder(order);
        p.setUser(user);
        p.setTimeMade(new Date());
        orderRepo.save(p);
    }

}
