package com.exercice1.demo.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.exercice1.demo.model.Order;
import com.exercice1.demo.model.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long customerId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Double totalAmount;
    private List<OrderItemResponse> items;
    public OrderResponse(Order order){
        this.id = order.getId();
        this.customerId = order.getCustomer().getId();
        this.items = order.getItems().stream().map(OrderItemResponse::new).collect(Collectors.toList());
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();
        this.totalAmount = order.getTotalAmount();
    }
}
