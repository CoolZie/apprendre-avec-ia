package com.exercice1.demo.dto;

import com.exercice1.demo.model.OrderItem;
import com.exercice1.demo.model.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long orderId;
    private Product product;
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;
    public OrderItemResponse(OrderItem orderItem){
        this.id = orderItem.getId();
        this.orderId = orderItem.getOrder().getId();
        this.product = orderItem.getProduct();
        this.quantity = orderItem.getQuantity();
        this.subtotal = orderItem.getSubtotal();
        this.unitPrice =  orderItem.getSubtotal();
    }
} 
