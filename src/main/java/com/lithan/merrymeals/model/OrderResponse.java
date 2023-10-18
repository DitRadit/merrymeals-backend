package com.lithan.merrymeals.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lithan.merrymeals.entity.Orders;
import com.lithan.merrymeals.entity.OrderItem;

import lombok.Data;

@Data
public class OrderResponse implements Serializable {
    
    private Long id;
    private String orderNumber;
    private Date date;
    private String userName;
    private String address;
    private Double latitude;  // Latitude information
    private Double longitude; // Longitude information 
    private Date time;
    private int amounts;
    private List<OrderResponse.Item> items;

    public OrderResponse(Orders order, List<OrderItem> orderItems){
        this.id = order.getId();
        this.orderNumber = order.getOrderNumber();
        this.date = order.getDate();
        this.userName = order.getUser().getUserName();
        this.address = order.getAddress();
        this.latitude = order.getLatitude();
        this.longitude = order.getLongitude();
        this.time = order.getTimeMade();
        this.amounts = orderItems.stream().mapToInt(OrderItem::getAmounts).sum();
        items = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            Item item = new Item();
            item.setMealId(orderItem.getMeal().getId());
            item.setFoodName(orderItem.getFoodName());
            item.setAmounts(orderItem.getAmounts());
            items.add(item);
        }
    }

    @Data
    public static class Item implements Serializable{
        private long mealId;
        private String foodName;
        private int amounts;

    }

}
