package com.lithan.merrymeals.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.lithan.merrymeals.model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Orders {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String orderNumber;
    @Temporal(TemporalType.DATE)
    private Date date;
    @JoinColumn
    @ManyToOne
    private MealsUsers user;
    private String address;
    private Double latitude;  // Latitude information
    private Double longitude; // Longitude information 
    @JoinColumn
    @ManyToOne
    private Meal meal;
    private int amounts;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeMade;
    private boolean addCaregiver;
    @JoinColumn
    @ManyToOne
    private MealsUsers careGiverName;


}
