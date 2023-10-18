package com.lithan.merrymeals.model;

import java.io.Serializable;

import java.util.List;

import lombok.Data;

@Data
public class OrderRequest implements Serializable {
    private String address;
    private Double latitude;  // Latitude information
    private Double longitude; // Longitude information
    private List<CartRequest> items;
    private boolean addCaregiver;
    private int amounts;
}
