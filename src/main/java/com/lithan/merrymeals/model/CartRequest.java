package com.lithan.merrymeals.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class CartRequest{

    private long mealId;
    private int amounts;
}
