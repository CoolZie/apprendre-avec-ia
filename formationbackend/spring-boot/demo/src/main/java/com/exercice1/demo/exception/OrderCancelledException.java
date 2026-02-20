package com.exercice1.demo.exception;

public class OrderCancelledException extends RuntimeException {
    public OrderCancelledException(Long orderId) {
        super(String.format("La commande #%d a été annulée et ne peut être modifiée", orderId));
    }
}