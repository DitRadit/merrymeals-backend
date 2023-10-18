package com.lithan.merrymeals.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JoinColumn
    @ManyToOne
    private Orders order;
    @JoinColumn
    @ManyToOne
    private Meal meal;
    @JoinColumn
    @ManyToOne
    private MealsUsers user;
    private String description;
    private String foodName;
    private int amounts;
    

}
