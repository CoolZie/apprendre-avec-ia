package com.exercice1.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exercice1.demo.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    
}
