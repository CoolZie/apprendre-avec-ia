package com.exercice1.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.exercice1.demo.model.Order;
import com.exercice1.demo.model.enums.OrderStatus;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status != 'CANCELLED'")
    Double calculateTotalRevenue();
}
